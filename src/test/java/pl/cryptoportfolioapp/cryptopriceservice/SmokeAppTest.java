package pl.cryptoportfolioapp.cryptopriceservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.cryptoportfolioapp.cryptopriceservice.container.MySqlTestContainer;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;
import pl.cryptoportfolioapp.cryptopriceservice.repository.PriceRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SmokeAppTest extends MySqlTestContainer {

    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;
    @Autowired
    private PriceRepository priceRepository;

    @Test
    void shouldInitiateRepoClasses() {
        assertThat(cryptocurrencyRepository).isNotNull();
        assertThat(priceRepository).isNotNull();
    }

}
