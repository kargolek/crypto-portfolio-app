package pl.cryptoportfolioapp.cryptopriceservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import pl.cryptoportfolioapp.cryptopriceservice.exception.PriceNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;
import pl.cryptoportfolioapp.cryptopriceservice.repository.PriceRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(MockitoExtension.class)
class PriceServiceUnitTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    private Price price;

    @BeforeEach
    void setUp() {
        var cryptocurrency = Cryptocurrency.builder()
                .id(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(RandomUtils.nextLong())
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        price = Price.builder()
                .id(1L)
                .priceCurrent(new BigDecimal("0.0000000001"))
                .percentChange1h(new BigDecimal("1.1"))
                .percentChange24h(new BigDecimal("2.1"))
                .percentChange7d(new BigDecimal("3.1"))
                .percentChange30d(new BigDecimal("4.1"))
                .percentChange60d(new BigDecimal("5.1"))
                .percentChange90d(new BigDecimal("6.1"))
                .cryptocurrency(cryptocurrency)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        cryptocurrency.setPrice(price);
    }

    @Test
    void whenUpdateCurrentPrice_thenShouldUpdateSuccessful() {
        when(priceRepository.findById(1L))
                .thenReturn(Optional.of(price));
        when(priceRepository.save(price))
                .thenReturn(price);
        price.setPriceCurrent(new BigDecimal("1.01"));

        var expected = priceService.update(price);

        assertThat(expected.getPriceCurrent())
                .isEqualTo("1.01");
    }

    @Test
    void whenUpdatePriceWithWrongId_thenThrowPriceNotFound() {
        when(priceRepository.findById(1L))
                .thenThrow(new PriceNotFoundException(1L));

        price.setPriceCurrent(new BigDecimal("1.01"));

        assertThatThrownBy(() -> priceService.update(price))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessage("Unable to find price with id: 1");
    }
}