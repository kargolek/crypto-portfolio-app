package pl.cryptoportfolioapp.cryptopriceservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import pl.cryptoportfolioapp.cryptopriceservice.container.MySqlTestContainer;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CryptocurrencyServiceIntegrationTest extends MySqlTestContainer {

    @Autowired
    private CryptocurrencyService cryptocurrencyService;
    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;

    private Cryptocurrency cryptocurrency;

    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(RandomUtils.nextLong())
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        System.out.printf("DB size: %d%n", cryptocurrencyRepository.findAll().size());
    }

    @Test
    void whenAddCryptocurrency_thenShouldSaveSuccessful() {
        var underTest = cryptocurrencyService.addCryptocurrency(cryptocurrency);
        cryptocurrencyRepository.findOne(Example.of(underTest))
                .ifPresent(crypto -> {
                    assertThat(underTest.getName())
                            .isEqualTo(crypto.getName());
                    assertThat(underTest.getSymbol())
                            .isEqualTo(crypto.getSymbol());
                    assertThat(underTest.getCoinMarketId())
                            .isEqualTo(crypto.getCoinMarketId());
                    assertThat(underTest.getLastUpdate())
                            .isEqualToIgnoringNanos(crypto.getLastUpdate());
                });
    }

    @Test
    void whenAddCryptoWithTheSameCoinMarketId_thenShouldThrowDataIntegrityException() {
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
        cryptocurrencyService.addCryptocurrency(crypto);

        assertThatThrownBy(() -> cryptocurrencyService.addCryptocurrency(crypto2))
                .isInstanceOf(DataIntegrityViolationException.class);
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

        var cryptoUpdated =
                cryptocurrencyService.updateCryptocurrency(cryptoToUpdate.getId(), cryptoUpdate);

        assertThat(cryptoUpdated)
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
                cryptocurrencyService.updateCryptocurrency(
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

        cryptocurrencyService.deleteCryptocurrency(id);

        assertThat(cryptocurrencyRepository.findAll())
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName
                ).doesNotContain(
                        tuple(id, "Ethereum")
                );
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

        var underTest = cryptocurrencyService.getByName("Ethereum");

        assertThat(underTest)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId
                ).containsExactly(cryptoSaved.getId(), "Ethereum", "ETH", 1L);
    }


}
