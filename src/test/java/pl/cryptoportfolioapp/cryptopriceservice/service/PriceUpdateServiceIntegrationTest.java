package pl.cryptoportfolioapp.cryptopriceservice.service;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.cryptoportfolioapp.cryptopriceservice.exception.MarketApiClientException;
import pl.cryptoportfolioapp.cryptopriceservice.extension.MockWebServerExtension;
import pl.cryptoportfolioapp.cryptopriceservice.extension.MySqlTestContainerExtension;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static pl.cryptoportfolioapp.cryptopriceservice.extension.MockWebServerExtension.mockWebServer;

/**
 * @author Karol Kuta-Orlowicz
 */

@ExtendWith(SpringExtension.class)
@ExtendWith(MySqlTestContainerExtension.class)
@ExtendWith(MockWebServerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("IntegrationTest")
class PriceUpdateServiceIntegrationTest {

    @Autowired
    private PriceUpdateService underTest;

    @Autowired
    CryptocurrencyService cryptocurrencyService;

    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;
    private Cryptocurrency btc;
    private Cryptocurrency eth;

    @DynamicPropertySource
    static void registerProperty(DynamicPropertyRegistry registry) {
        registry.add("api.coin.market.cap.baseUrl", () -> mockWebServer.url("/").toString());
    }

    @BeforeEach
    void setUp() {

        var priceBtc = Price.builder()
                .id(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        btc = Cryptocurrency.builder()
                .id(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(1L)
                .price(priceBtc)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        priceBtc.setCryptocurrency(btc);

        var priceEth = Price.builder()
                .id(2L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        eth = Cryptocurrency.builder()
                .id(2L)
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(1027L)
                .price(priceEth)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        priceEth.setCryptocurrency(eth);

    }

    @AfterEach
    void tearDown() {
        cryptocurrencyRepository.deleteAll();
    }

    @Test
    void whenReceiveOneCryptoPriceUpdate_thenPricesShouldBeUpdatedForOneEntity() {
        cryptocurrencyRepository.save(btc);
        cryptocurrencyRepository.save(eth);

        var bodyRes = """
                {
                "data": {
                "1": {
                "id": 1,
                "name": "Bitcoin",
                "symbol": "BTC",
                "slug": "bitcoin",
                "is_active": 1,
                "is_fiat": 0,
                "circulating_supply": 17199862,
                "total_supply": 17199862,
                "max_supply": 21000000,
                "date_added": "2013-04-28T00:00:00.000Z",
                "num_market_pairs": 331,
                "cmc_rank": 1,
                "last_updated": "2018-08-09T21:56:28.000Z",
                "tags": [
                "mineable"
                ],
                "platform": null,
                "self_reported_circulating_supply": null,
                "self_reported_market_cap": null,
                "quote": {
                "USD": {
                "price": 6602.60701122,
                "volume_24h": 4314444687.5194,
                "volume_change_24h": -0.152774,
                "percent_change_1h": 0.988615,
                "percent_change_24h": 4.37185,
                "percent_change_7d": -12.1352,
                "percent_change_30d": -12.1352,
                "market_cap": 852164659250.2758,
                "market_cap_dominance": 51,
                "fully_diluted_market_cap": 952835089431.14,
                "last_updated": "2018-08-09T21:56:28.000Z"
                }
                }
                }
                },
                "status": {
                "timestamp": "2023-01-03T19:55:35.426Z",
                "error_code": 0,
                "error_message": "",
                "elapsed": 10,
                "credit_count": 1
                }
                }""";
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        underTest.updateCryptocurrencyPrices();

        assertThat(cryptocurrencyRepository.findAll()
                .stream().map(Cryptocurrency::getPrice).toList()).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h
        ).containsExactly(
                tuple(
                        new BigDecimal("6602.607011220000"),
                        new BigDecimal("0.988615000000"),
                        new BigDecimal("4.371850000000")
                ),
                tuple(
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void whenReceiveTwoCryptoPriceUpdate_thenPricesShouldBeUpdatedForTwoEntities() {
        cryptocurrencyRepository.save(btc);
        cryptocurrencyRepository.save(eth);

        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-16T09:23:08.500Z",
                        "error_code": 0,
                        "error_message": null,
                        "elapsed": 40,
                        "credit_count": 1,
                        "notice": null
                    },
                    "data": {
                        "1": {
                            "id": 1,
                            "name": "Bitcoin",
                            "symbol": "BTC",
                            "slug": "bitcoin",
                            "num_market_pairs": 9931,
                            "date_added": "2013-04-28T00:00:00.000Z",
                            "max_supply": 21000000,
                            "circulating_supply": 19263793,
                            "total_supply": 19263793,
                            "is_active": 1,
                            "platform": null,
                            "cmc_rank": 1,
                            "is_fiat": 0,
                            "self_reported_circulating_supply": null,
                            "self_reported_market_cap": null,
                            "tvl_ratio": null,
                            "last_updated": "2023-01-16T09:21:00.000Z",
                            "quote": {
                                "USD": {
                                    "price": 20811.843148731245,
                                    "volume_24h": 23379448071.008915,
                                    "volume_change_24h": -13.9075,
                                    "percent_change_1h": -0.13446372,
                                    "percent_change_24h": 0.89816983,
                                    "percent_change_7d": 20.92671809,
                                    "percent_change_30d": 24.23402424,
                                    "percent_change_60d": 25.6269808,
                                    "percent_change_90d": 6.47186409,
                                    "market_cap": 400915038365.6269,
                                    "market_cap_dominance": 40.9945,
                                    "fully_diluted_market_cap": 437048706123.36,
                                    "tvl": null,
                                    "last_updated": "2023-01-16T09:21:00.000Z"
                                }
                            }
                        },
                        "1027": {
                                    "id": 1027,
                                    "name": "Ethereum",
                                    "symbol": "ETH",
                                    "slug": "ethereum",
                                    "num_market_pairs": 6360,
                                    "date_added": "2015-08-07T00:00:00.000Z",
                                    "max_supply": null,
                                    "circulating_supply": 122373866.2178,
                                    "total_supply": 122373866.2178,
                                    "is_active": 1,
                                    "platform": null,
                                    "cmc_rank": 2,
                                    "is_fiat": 0,
                                    "self_reported_circulating_supply": null,
                                    "self_reported_market_cap": null,
                                    "tvl_ratio": null,
                                    "last_updated": "2023-01-16T09:35:00.000Z",
                                    "quote": {
                                        "USD": {
                                            "price": 1543.3040228407024,
                                            "volume_24h": 7685876434.055611,
                                            "volume_change_24h": -6.5169,
                                            "percent_change_1h": 0.13288047,
                                            "percent_change_24h": 1.15698649,
                                            "percent_change_7d": 17.35844656,
                                            "percent_change_30d": 30.80542595,
                                            "percent_change_60d": 29.38690869,
                                            "percent_change_90d": 16.63344566,
                                            "market_cap": 188860080024.50067,
                                            "market_cap_dominance": 19.2937,
                                            "fully_diluted_market_cap": 188860080024.5,
                                            "tvl": null,
                                            "last_updated": "2023-01-16T09:35:00.000Z"
                                        }
                                    }
                                }
                    }
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        underTest.updateCryptocurrencyPrices();

        var prices = cryptocurrencyRepository.findAll()
                .stream()
                .map(Cryptocurrency::getPrice)
                .toList();

        assertThat(prices).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d
        ).containsExactly(
                tuple(
                        new BigDecimal("20811.843148731245"),
                        new BigDecimal("-0.134463720000"),
                        new BigDecimal("0.898169830000"),
                        new BigDecimal("20.926718090000")
                ),
                tuple(
                        new BigDecimal("1543.304022840702"),
                        new BigDecimal("0.132880470000"),
                        new BigDecimal("1.156986490000"),
                        new BigDecimal("17.358446560000")
                )
        );
    }

    @Test
    void whenReceiveNoCryptoPriceUpdate_thenPriceShouldNotBeUpdate() {
        cryptocurrencyRepository.save(btc);
        cryptocurrencyRepository.save(eth);

        var bodyRes = """
                {
                    "status": {
                        "timestamp": "2023-01-16T09:23:08.500Z",
                        "error_code": 0,
                        "error_message": null,
                        "elapsed": 40,
                        "credit_count": 1,
                        "notice": null
                    }
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        underTest.updateCryptocurrencyPrices();

        assertThat(cryptocurrencyRepository.findAll()
                .stream().map(Cryptocurrency::getPrice).toList()).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d
        ).containsExactly(
                tuple(
                        null,
                        null,
                        null,
                        null
                ),
                tuple(
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void whenReceiveResponse500_thenPriceShouldNotBeUpdate() {
        cryptocurrencyRepository.save(btc);
        cryptocurrencyRepository.save(eth);

        var bodyRes = """
                {
                "status": {
                "timestamp": "2018-06-02T22:51:28.209Z",
                "error_code": 500,
                "error_message": "An internal server error occurred",
                "elapsed": 10,
                "credit_count": 0
                }
                }""";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        assertThatThrownBy(() -> underTest.updateCryptocurrencyPrices())
                .isInstanceOf(MarketApiClientException.class);

        assertThat(cryptocurrencyRepository.findAll()
                .stream().map(Cryptocurrency::getPrice).toList()).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d
        ).containsExactly(
                tuple(
                        null,
                        null,
                        null,
                        null
                ),
                tuple(
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void whenCryptocurrencyRepoEmpty_thenReturnEmptyList() {
        var expected = underTest.updateCryptocurrencyPrices();

        assertThat(expected).hasSize(0);
    }
}