package pl.cryptoportfolioapp.cryptopriceservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.cryptoportfolioapp.cryptopriceservice.container.MySqlTestContainer;
import pl.cryptoportfolioapp.cryptopriceservice.exception.PriceNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;
import pl.cryptoportfolioapp.cryptopriceservice.repository.PriceRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PriceServiceIntegrationTest extends MySqlTestContainer {

    @Autowired
    private PriceService underTestService;
    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @Autowired
    private PriceRepository priceRepository;

    private Cryptocurrency cryptocurrency;

    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(1L)
                .build();
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void whenUpdatePrice_thenPriceShouldUpdatedSuccessful() {
        var cryptoEntity = cryptocurrencyService.addCryptocurrency(cryptocurrency);
        var priceEntity = cryptoEntity.getPrice();
        priceEntity.setPriceCurrent(new BigDecimal("100100100100.1002"));

        underTestService.update(priceEntity);
        var expected = priceRepository.findById(priceEntity.getId());

        assertThat(expected)
                .get()
                .extracting(
                        Price::getId,
                        Price::getPriceCurrent,
                        Price::getPercentChange1h
                ).containsExactly(
                        priceEntity.getId(),
                        new BigDecimal("100100100100.100200000000"),
                        null
                );

        assertThat(expected.map(Price::getLastUpdate).orElseThrow())
                .isEqualToIgnoringNanos(priceEntity.getLastUpdate());
    }

    @Test
    void whenNoPriceAndUpdatePrice_thenThrowPriceNotFound() {
        var price = Price.builder()
                .id(100L)
                .priceCurrent(new BigDecimal("1200.01"))
                .cryptocurrency(cryptocurrency)
                .build();

        assertThatThrownBy(() -> underTestService.update(price))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessage("Unable to find price with id: 100");
    }
}