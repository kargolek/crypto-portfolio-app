package pl.cryptoportfolioapp.cryptopriceservice;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.cryptoportfolioapp.cryptopriceservice.container.MySqlTestContainer;
import pl.cryptoportfolioapp.cryptopriceservice.controller.CryptocurrencyController;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;
import pl.cryptoportfolioapp.cryptopriceservice.repository.PriceRepository;
import pl.cryptoportfolioapp.cryptopriceservice.service.CryptocurrencyService;
import pl.cryptoportfolioapp.cryptopriceservice.service.PriceService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("SmokeTest")
class SmokeAppTest extends MySqlTestContainer {

    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private CryptocurrencyService cryptocurrencyService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private CryptocurrencyController cryptocurrencyController;

    @Test
    void contextLoad() {
        assertThat(cryptocurrencyRepository).isNotNull();
        assertThat(priceRepository).isNotNull();
        assertThat(cryptocurrencyService).isNotNull();
        assertThat(priceService).isNotNull();
        assertThat(cryptocurrencyController).isNotNull();
    }

}
