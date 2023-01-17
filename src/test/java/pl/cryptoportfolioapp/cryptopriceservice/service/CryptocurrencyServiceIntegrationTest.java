package pl.cryptoportfolioapp.cryptopriceservice.service;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import pl.cryptoportfolioapp.cryptopriceservice.container.MySqlTestContainer;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;
import pl.cryptoportfolioapp.cryptopriceservice.repository.PriceRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("IntegrationTest")
public class CryptocurrencyServiceIntegrationTest extends MySqlTestContainer {

    @Autowired
    private CryptocurrencyService underTestService;
    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;

    @Autowired
    private PriceRepository priceRepository;

    private Cryptocurrency cryptocurrency;

    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(RandomUtils.nextLong())
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }

    @Test
    void whenAddCryptocurrency_thenShouldSaveSuccessful() {
        var expected = underTestService.addCryptocurrency(cryptocurrency);
        assertThat(cryptocurrencyRepository.findAll())
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate
                ).containsExactly(
                        tuple(
                                expected.getId(),
                                expected.getName(),
                                expected.getSymbol(),
                                expected.getCoinMarketId(),
                                expected.getLastUpdate()
                        )
                );
    }

    @Test
    void whenAddCryptoWithTheSameCoinMarketId_thenShouldThrowConstraintViolation() {
        var coinMarketIdRandom = RandomUtils.nextLong();
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(coinMarketIdRandom)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        var crypto2 = Cryptocurrency.builder()
                .name("Chia")
                .symbol("XCH")
                .coinMarketId(coinMarketIdRandom)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        underTestService.addCryptocurrency(crypto);

        assertThatThrownBy(() -> underTestService.addCryptocurrency(crypto2))
                .hasCauseInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void whenUpdateByCorrectId_thenShouldUpdateSuccessful() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var cryptoToUpdate = cryptocurrencyRepository.save(crypto);

        var cryptoUpdate = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(2L)
                .build();

        var expected =
                underTestService.updateCryptocurrency(cryptoToUpdate.getId(), cryptoUpdate);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId)
                .containsExactly(cryptoToUpdate.getId(), "Bitcoin", "BTC", 2L);
    }

    @Test
    void whenUpdateByIncorrectId_thenShouldThrowAnException() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var cryptoToUpdate = cryptocurrencyRepository.save(crypto);

        var cryptoUpdate = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(2L)
                .build();

        var searchedId = cryptoToUpdate.getId() + 10L;

        assertThatThrownBy(() ->
                underTestService.updateCryptocurrency(
                        searchedId,
                        cryptoUpdate))
                .isInstanceOf(CryptocurrencyNotFoundException.class)
                .hasMessageContaining("id: " + searchedId);
    }

    @Test
    void whenDeleteById_thenShouldDeleteCryptocurrency() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var id = cryptocurrencyRepository.save(crypto).getId();

        underTestService.deleteCryptocurrency(id);

        assertThat(cryptocurrencyRepository.findAll())
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName
                ).doesNotContain(
                        tuple(id, "Ethereum")
                );
    }

    @Test
    void whenDeleteByIdNotExisted_thenThrowEmptyResultDataAccessExc() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var id = cryptocurrencyRepository.save(crypto);

        assertThatThrownBy(() -> underTestService.deleteCryptocurrency(100L))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessage("No class pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency entity with id 100 exists!");
    }

    @Test
    void whenFindByName_thenFindShouldBeSuccessful() {
        var crypto = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        var cryptoSaved = cryptocurrencyRepository.save(crypto);

        var expected = underTestService.getByName(List.of("Ethereum", "Bitcoin"));

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId
                ).containsExactly(
                        tuple(
                                cryptoSaved.getId(),
                                "Ethereum",
                                "ETH",
                                1L
                        )
                );
    }

    @Test
    void whenSaveCryptocurrencyEntity_thenPriceEntityShouldSaveAlso() {
        underTestService.addCryptocurrency(cryptocurrency);

        assertThat(priceRepository.findAll())
                .hasSize(1)
                .extracting(
                        Price::getId,
                        Price::getPriceCurrent,
                        Price::getPercentChange1h,
                        Price::getCryptocurrency
                ).containsExactly(
                        tuple(
                                cryptocurrency.getPrice().getId(), null, null, cryptocurrency
                        )
                );
    }
}
