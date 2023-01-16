package pl.cryptoportfolioapp.cryptopriceservice.service;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.CryptocurrencyResponseDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.PriceResponseClientDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.PriceResponseDTO;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Karol Kuta-Orlowicz
 */
@ExtendWith(MockitoExtension.class)
class PriceUpdateServiceUnitTest {
    @Mock
    private CryptocurrencyService cryptocurrencyService;

    @Mock
    private PriceServiceClient priceServiceClient;

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceUpdateService underTest;

    private List<Cryptocurrency> cryptocurrencyEntities;
    private PriceResponseClientDTO priceResponseClientDTO;
    private PriceResponseDTO priceResponseBTC;
    private PriceResponseDTO priceResponseETH;
    private Price priceETH;
    private Price priceBTC;

    @BeforeEach
    void setUp() {
        final String btcName = "Bitcoin";
        final String btcSymbol = "BTC";
        final long btcMarketId = 1L;
        var bitcoin = Cryptocurrency.builder()
                .id(1L)
                .name(btcName)
                .symbol(btcSymbol)
                .coinMarketId(btcMarketId)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        final String ethName = "Ethereum";
        final String ethSymbol = "ETH";
        final long ethMarketId = 1027L;
        var ethereum = Cryptocurrency.builder()
                .id(2L)
                .name(ethName)
                .symbol(ethSymbol)
                .coinMarketId(ethMarketId)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        priceBTC = Price.builder()
                .id(1L)
                .priceCurrent(new BigDecimal("20000.5"))
                .percentChange1h(new BigDecimal("0.5"))
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .cryptocurrency(bitcoin)
                .build();
        bitcoin.setPrice(priceBTC);

        priceETH = Price.builder()
                .id(2L)
                .priceCurrent(new BigDecimal("1500.5"))
                .percentChange1h(new BigDecimal("-1.5"))
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .cryptocurrency(ethereum)
                .build();
        ethereum.setPrice(priceETH);

        cryptocurrencyEntities = List.of(bitcoin, ethereum);

        priceResponseBTC = new PriceResponseDTO()
                .setPriceCurrent(new BigDecimal("21000.5"))
                .setPercentChange1h(new BigDecimal("5.0"))
                .setPercentChange24h(new BigDecimal("7.5"));
        priceResponseETH = new PriceResponseDTO()
                .setPriceCurrent(new BigDecimal("2000.5"))
                .setPercentChange1h(new BigDecimal("25.5"))
                .setPercentChange24h(new BigDecimal("40.5"));

        var cryptoBTC = new CryptocurrencyResponseDTO()
                .setName(btcName)
                .setSymbol(btcSymbol)
                .setCoinMarketId(btcMarketId)
                .setQuote(Map.of("USD", priceResponseBTC));
        var cryptoETH = new CryptocurrencyResponseDTO()
                .setName(ethName)
                .setSymbol(ethSymbol)
                .setCoinMarketId(ethMarketId)
                .setQuote(Map.of("USD", priceResponseETH));

        priceResponseClientDTO = new PriceResponseClientDTO()
                .setData(Map.of("1", cryptoBTC, "1027", cryptoETH));

    }

    @Test
    void whenUpdateCryptocurrencyPrices_thenPricesShouldBeUpdated() {
        when(cryptocurrencyService.getCryptocurrencies())
                .thenReturn(cryptocurrencyEntities);
        when(priceServiceClient.getLatestPriceByIds(any()))
                .thenReturn(Optional.of(priceResponseClientDTO));
        var priceEntities = cryptocurrencyEntities.stream()
                .map(Cryptocurrency::getPrice)
                .toList();
        when(priceService.updatePrices(any()))
                .thenReturn(priceEntities);

        var expected = underTest.updateCryptocurrencyPrices();

        assertThat(expected)
                .extracting(
                        Price::getId,
                        Price::getPriceCurrent,
                        Price::getPercentChange1h,
                        Price::getPercentChange24h
                ).containsExactly(
                        Tuple.tuple(
                                priceBTC.getId(),
                                priceResponseBTC.getPriceCurrent(),
                                priceResponseBTC.getPercentChange1h(),
                                priceResponseBTC.getPercentChange24h()
                        ),
                        Tuple.tuple(
                                priceETH.getId(),
                                priceResponseETH.getPriceCurrent(),
                                priceResponseETH.getPercentChange1h(),
                                priceResponseETH.getPercentChange24h()
                        )
                );
    }

    @Test
    void whenCryptocurrenciesIsEmpty_thenReturnEmptyList() {
        when(cryptocurrencyService.getCryptocurrencies())
                .thenReturn(Collections.emptyList());

        var expected = underTest.updateCryptocurrencyPrices();

        assertThat(expected).hasSize(0);
    }
}