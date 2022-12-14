package pl.cryptoportfolioapp.cryptopriceservice.repository;

import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.cryptoportfolioapp.cryptopriceservice.container.MySqlTestContainer;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PriceRepositoryTest extends MySqlTestContainer {

    private Cryptocurrency cryptocurrency;
    private Price price;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private CryptocurrencyRepository cryptoRepository;


    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now())
                .build();
        cryptoRepository.save(cryptocurrency);

        price = Price.builder()
                .priceCurrent(new BigDecimal("0.0000000000812"))
                .percentChange1h(new BigDecimal("1.1"))
                .percentChange24h(new BigDecimal("2.1"))
                .cryptocurrencyId(cryptocurrency)
                .lastUpdate(LocalDateTime.now())
                .build();
        priceRepository.save(price);
    }

    @Test
    void shouldReturnSavedPrice() {
        //when
        var result = priceRepository.getReferenceById(price.getId());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId())
                .isGreaterThan(0);
        assertThat(result.getPriceCurrent())
                .isEqualTo(price.getPriceCurrent());
    }

    @Test
    void shouldFindAllPrices() {
        //when
        var prices = priceRepository.findAll();
        //then
        assertThat(prices)
                .hasSizeGreaterThan(0)
                .extracting(Price::getPriceCurrent)
                .containsOnly(price.getPriceCurrent());
    }

    @Test
    void shouldDeletePrice() {
        //when
        priceRepository.delete(price);
        //then
        var prices = priceRepository.findAll();
        assertThat(prices)
                .hasSize(0);
    }

    @Test
    void shouldNotSavePriceEntityWithNullPriceCurrent() {
        //given
        var underTest = Price.builder()
                .priceCurrent(null)
                .cryptocurrencyId(cryptocurrency)
                .lastUpdate(LocalDateTime.now())
                .build();

        //when//then
        assertThatThrownBy(() -> priceRepository.save(underTest))
                .hasCauseInstanceOf(PropertyValueException.class);
    }

    @Test
    void shouldNotSavePriceEntityWithNullCryptocurrencyId() {
        //given
        var underTest = Price.builder()
                .priceCurrent(new BigDecimal("1.0"))
                .cryptocurrencyId(null)
                .lastUpdate(LocalDateTime.now())
                .build();

        //when//then
        assertThatThrownBy(() -> priceRepository.save(underTest))
                .hasCauseInstanceOf(PropertyValueException.class);
    }

}