package pl.cryptoportfolioapp.cryptopriceservice.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.CryptocurrencyResponseDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.PriceResponseDTO;
import pl.cryptoportfolioapp.cryptopriceservice.exception.PriceServiceClientException;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Karol Kuta-Orlowicz
 */
@Tag("UnitTest")
class PriceServiceClientTest {

    private final MockWebServer mockWebServer = new MockWebServer();
    private PriceServiceClient priceServiceClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("").toString())
                .build();
        priceServiceClient = new PriceServiceClient(webClient);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.close();
    }

    @Test
    void whenServerRespond200_thenClientReturnRespondDTOCollection() {
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

        var response = priceServiceClient.getLatestPriceByIds("1");
        var cryptoResponse = response.orElseThrow().getData().get("1");
        var priceResponse = response.orElseThrow().getData().get("1").getQuote().get("USD");

        assertThat(cryptoResponse)
                .extracting(
                        CryptocurrencyResponseDTO::getName,
                        CryptocurrencyResponseDTO::getSymbol,
                        CryptocurrencyResponseDTO::getCoinMarketId
                ).containsExactly(
                        "Bitcoin",
                        "BTC",
                        1L
                );

        assertThat(priceResponse).extracting(
                PriceResponseDTO::getPriceCurrent,
                PriceResponseDTO::getPercentChange1h,
                PriceResponseDTO::getPercentChange24h,
                PriceResponseDTO::getPercentChange7d,
                PriceResponseDTO::getPercentChange30d,
                PriceResponseDTO::getPercentChange60d,
                PriceResponseDTO::getPercentChange90d
        ).containsExactly(
                new BigDecimal("6602.60701122"),
                new BigDecimal("0.988615"),
                new BigDecimal("4.37185"),
                new BigDecimal("-12.1352"),
                new BigDecimal("-12.1352"),
                null,
                null
        );
    }

    @Test
    void whenServerRespond400_thenClientThrowCustomExc() {
        var bodyRes = """
                {
                "status": {
                "timestamp": "2018-06-02T22:51:28.209Z",
                "error_code": 400,
                "error_message": "Invalid value for \\"id\\"",
                "elapsed": 10,
                "credit_count": 0
                }
                }""";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(bodyRes)
        );

        assertThatThrownBy(() -> priceServiceClient.getLatestPriceByIds("1234567890"))
                .isInstanceOf(PriceServiceClientException.class)
                .hasMessageContaining("1234567890");
    }

    @Test
    void whenServerRespond500_thenClientThrowCustomExc() {
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

        assertThatThrownBy(() -> priceServiceClient.getLatestPriceByIds("1234567890"))
                .isInstanceOf(PriceServiceClientException.class)
                .hasMessageContaining("1234567890")
                .hasMessageContaining("serverMessage: An internal server error occurred");
    }
}