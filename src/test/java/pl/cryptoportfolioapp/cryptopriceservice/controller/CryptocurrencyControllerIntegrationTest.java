package pl.cryptoportfolioapp.cryptopriceservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.cryptoportfolioapp.cryptopriceservice.exception.JsonApiError;
import pl.cryptoportfolioapp.cryptopriceservice.extension.MockWebServerExtension;
import pl.cryptoportfolioapp.cryptopriceservice.extension.MySqlTestContainerExtension;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static pl.cryptoportfolioapp.cryptopriceservice.extension.MockWebServerExtension.mockWebServer;

/**
 * @author Karol Kuta-Orlowicz
 */

@ExtendWith(SpringExtension.class)
@ExtendWith(MySqlTestContainerExtension.class)
@ExtendWith(MockWebServerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("IntegrationTest")
public class CryptocurrencyControllerIntegrationTest {

    private static final String BTC_NAME = "Bitcoin";
    private static final String BTC_SYMBOL = "BTC";
    private static final long BTC_MARKET_ID = 1L;
    private static final String ETH_NAME = "Ethereum";
    private static final String ETH_SYMBOL = "ETH";
    private static final long ETH_MARKET_ID = 1027L;
    private static final BigDecimal BTC_PRICE = new BigDecimal("20000.50").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_PERCENT_1H = new BigDecimal("0.5").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_PERCENT_24H = new BigDecimal("-1.0").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_PERCENT_7D = new BigDecimal("1.5").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_PERCENT_30D = new BigDecimal("-2.0").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_PERCENT_60D = new BigDecimal("2.5").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal BTC_PERCENT_90D = new BigDecimal("-3.0").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PRICE = new BigDecimal("1800.50").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_1H = new BigDecimal("4.5").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_24H = new BigDecimal("-5.0").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_7D = new BigDecimal("5.5").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_30D = new BigDecimal("-6.0").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_60D = new BigDecimal("6.5").setScale(12, RoundingMode.HALF_UP);
    private static final BigDecimal ETH_PERCENT_90D = new BigDecimal("-7.0").setScale(12, RoundingMode.HALF_UP);
    @Autowired
    private TestRestTemplate template;

    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;

    private Long bitcoinID;
    private Long ethereumID;

    @DynamicPropertySource
    static void registerProperty(DynamicPropertyRegistry registry) {
        registry.add("api.coin.market.cap.baseUrl", () -> mockWebServer.url("/").toString());
    }

    @BeforeEach
    public void setup() {

        var cryptos = cryptocurrencyRepository.findAll();

        bitcoinID = cryptos.stream()
                .filter(cryptocurrency -> cryptocurrency.getName().equalsIgnoreCase(BTC_NAME))
                .map(Cryptocurrency::getId)
                .findFirst()
                .orElse(0L);

        ethereumID = cryptos.stream()
                .filter(cryptocurrency -> cryptocurrency.getName().equalsIgnoreCase(ETH_NAME))
                .map(Cryptocurrency::getId)
                .findFirst()
                .orElse(0L);
    }

    @Test
    @Sql({"/delete_data.sql", "/insert_data.sql"})
    void whenGetCryptos_thenReturnBodyWithProperData() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency", Cryptocurrency[].class);

        var cryptocurrencies = stream(requireNonNull(responseEntity.getBody())).toList();
        var prices = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .toList();
        var cryptoIds = cryptocurrencies.stream()
                .map(Cryptocurrency::getId)
                .toList();
        var priceIds = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .map(Price::getId)
                .toList();

        var cryptosDateTime = cryptocurrencies.stream()
                .map(Cryptocurrency::getLastUpdate)
                .toList();

        var pricesDateTime = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .map(Price::getLastUpdate)
                .toList();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(cryptoIds)
                .allMatch(aLong -> aLong > 0L);

        assertThat(cryptocurrencies).extracting(
                Cryptocurrency::getName,
                Cryptocurrency::getSymbol,
                Cryptocurrency::getCoinMarketId
        ).containsExactly(
                tuple(
                        BTC_NAME,
                        BTC_SYMBOL,
                        BTC_MARKET_ID
                ),
                tuple(
                        ETH_NAME,
                        ETH_SYMBOL,
                        ETH_MARKET_ID
                )
        );

        assertThat(cryptosDateTime)
                .allMatch(localDateTime -> localDateTime.isBefore(LocalDateTime.now()));

        assertThat(priceIds)
                .allMatch(aLong -> aLong > 0L);

        assertThat(prices).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        BTC_PRICE,
                        BTC_PERCENT_1H,
                        BTC_PERCENT_24H,
                        BTC_PERCENT_7D,
                        BTC_PERCENT_30D,
                        BTC_PERCENT_60D,
                        BTC_PERCENT_90D),
                tuple(
                        ETH_PRICE,
                        ETH_PERCENT_1H,
                        ETH_PERCENT_24H,
                        ETH_PERCENT_7D,
                        ETH_PERCENT_30D,
                        ETH_PERCENT_60D,
                        ETH_PERCENT_90D)
        );

        assertThat(pricesDateTime)
                .allMatch(localDateTime -> localDateTime.isBefore(LocalDateTime.now()));
    }

    @Test
    @Sql({"/delete_data.sql"})
    void whenGetCryptosAndDBEmpty_thenReturnEmptyBody() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("[]");
    }

    @Test
    @Sql({"/delete_data.sql"})
    void whenGetCryptosByQueryNamesAndDBEmpty_thenReturnEmptyBody() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency?name=Bitcoin,Ethereum", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("[]");
    }

    @Test
    @Sql({"/delete_data.sql", "/insert_data.sql"})
    void whenGetCryptosByQueryNames_thenReturnBodyWithProperData() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency?name=Bitcoin,Ethereum", Cryptocurrency[].class);

        var cryptocurrencies = stream(requireNonNull(responseEntity.getBody())).toList();
        var prices = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .toList();
        var cryptoIds = cryptocurrencies.stream()
                .map(Cryptocurrency::getId)
                .toList();
        var priceIds = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .map(Price::getId)
                .toList();

        var cryptosDateTime = cryptocurrencies.stream()
                .map(Cryptocurrency::getLastUpdate)
                .toList();

        var pricesDateTime = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .map(Price::getLastUpdate)
                .toList();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(cryptoIds)
                .allMatch(aLong -> aLong > 0L);

        assertThat(cryptocurrencies).extracting(
                Cryptocurrency::getName,
                Cryptocurrency::getSymbol,
                Cryptocurrency::getCoinMarketId
        ).containsExactly(
                tuple(
                        BTC_NAME,
                        BTC_SYMBOL,
                        BTC_MARKET_ID
                ),
                tuple(
                        ETH_NAME,
                        ETH_SYMBOL,
                        ETH_MARKET_ID
                )
        );

        assertThat(cryptosDateTime)
                .allMatch(localDateTime -> localDateTime.isBefore(LocalDateTime.now()));

        assertThat(priceIds)
                .allMatch(aLong -> aLong > 0L);

        assertThat(prices).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        BTC_PRICE,
                        BTC_PERCENT_1H,
                        BTC_PERCENT_24H,
                        BTC_PERCENT_7D,
                        BTC_PERCENT_30D,
                        BTC_PERCENT_60D,
                        BTC_PERCENT_90D),
                tuple(
                        ETH_PRICE,
                        ETH_PERCENT_1H,
                        ETH_PERCENT_24H,
                        ETH_PERCENT_7D,
                        ETH_PERCENT_30D,
                        ETH_PERCENT_60D,
                        ETH_PERCENT_90D)
        );

        assertThat(pricesDateTime)
                .allMatch(localDateTime -> localDateTime.isBefore(LocalDateTime.now()));
    }

    @Test
    @Sql({"/delete_data.sql", "/insert_data.sql"})
    void whenGetCryptosByNotExistQueryNames_thenReturnBodyWithProperData() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency?name=NotExist,NotExist2", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("[]");
    }

    @Test
    @Sql({"/delete_data.sql", "/insert_data.sql"})
    void whenGetCryptosBySecondExistQueryName_thenReturnBodyWithOneCryptoData() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency?name=NotExist,Ethereum", Cryptocurrency[].class);

        var cryptocurrencies = stream(requireNonNull(responseEntity.getBody())).toList();
        var prices = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .toList();
        var cryptoIds = cryptocurrencies.stream()
                .map(Cryptocurrency::getId)
                .toList();
        var priceIds = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .map(Price::getId)
                .toList();

        var cryptosDateTime = cryptocurrencies.stream()
                .map(Cryptocurrency::getLastUpdate)
                .toList();

        var pricesDateTime = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .map(Price::getLastUpdate)
                .toList();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(cryptoIds)
                .allMatch(aLong -> aLong > 0L);

        assertThat(cryptocurrencies).extracting(
                Cryptocurrency::getName,
                Cryptocurrency::getSymbol,
                Cryptocurrency::getCoinMarketId
        ).containsExactly(
                tuple(
                        ETH_NAME,
                        ETH_SYMBOL,
                        ETH_MARKET_ID
                )
        );

        assertThat(cryptosDateTime)
                .allMatch(localDateTime -> localDateTime.isBefore(LocalDateTime.now()));

        assertThat(priceIds)
                .allMatch(aLong -> aLong > 0L);

        assertThat(prices).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        ETH_PRICE,
                        ETH_PERCENT_1H,
                        ETH_PERCENT_24H,
                        ETH_PERCENT_7D,
                        ETH_PERCENT_30D,
                        ETH_PERCENT_60D,
                        ETH_PERCENT_90D)
        );

        assertThat(pricesDateTime)
                .allMatch(localDateTime -> localDateTime.isBefore(LocalDateTime.now()));
    }

    @Test
    @Sql({"/delete_data.sql", "/insert_data.sql"})
    void whenGetCryptosByFirstExistQueryName_thenReturnBodyOneCryptoData() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency?name=Bitcoin,NotExist", Cryptocurrency[].class);

        var cryptocurrencies = stream(requireNonNull(responseEntity.getBody())).toList();
        var prices = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .toList();
        var cryptoIds = cryptocurrencies.stream()
                .map(Cryptocurrency::getId)
                .toList();
        var priceIds = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .map(Price::getId)
                .toList();

        var cryptosDateTime = cryptocurrencies.stream()
                .map(Cryptocurrency::getLastUpdate)
                .toList();

        var pricesDateTime = cryptocurrencies.stream()
                .map(Cryptocurrency::getPrice)
                .map(Price::getLastUpdate)
                .toList();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(cryptoIds)
                .allMatch(aLong -> aLong > 0L);

        assertThat(cryptocurrencies).extracting(
                Cryptocurrency::getName,
                Cryptocurrency::getSymbol,
                Cryptocurrency::getCoinMarketId
        ).containsExactly(
                tuple(
                        BTC_NAME,
                        BTC_SYMBOL,
                        BTC_MARKET_ID
                )
        );

        assertThat(cryptosDateTime)
                .allMatch(localDateTime -> localDateTime.isBefore(LocalDateTime.now()));

        assertThat(priceIds)
                .allMatch(aLong -> aLong > 0L);

        assertThat(prices).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                tuple(
                        BTC_PRICE,
                        BTC_PERCENT_1H,
                        BTC_PERCENT_24H,
                        BTC_PERCENT_7D,
                        BTC_PERCENT_30D,
                        BTC_PERCENT_60D,
                        BTC_PERCENT_90D)
        );

        assertThat(pricesDateTime)
                .allMatch(localDateTime -> localDateTime.isBefore(LocalDateTime.now()));
    }

    @Test
    @Sql({"/delete_data.sql", "/insert_data.sql"})
    void whenGetCryptosByEmptyQueryName_thenReturnEmptyBody() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency?name=", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("[]");
    }

    @Test
    @Sql({"/delete_data.sql", "/insert_data.sql"})
    void whenGetCryptoByID_thenReturnBodyWithCryptoData() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency/" + bitcoinID,
                Cryptocurrency.class);

        var crypto = Objects.requireNonNull(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(crypto).extracting(
                Cryptocurrency::getId,
                Cryptocurrency::getName,
                Cryptocurrency::getSymbol,
                Cryptocurrency::getCoinMarketId
        ).containsExactly(
                bitcoinID,
                BTC_NAME,
                BTC_SYMBOL,
                BTC_MARKET_ID
        );

        assertThat(crypto.getLastUpdate())
                .isBefore(LocalDateTime.now());

        assertThat(crypto.getPrice()).extracting(
                Price::getPriceCurrent,
                Price::getPercentChange1h,
                Price::getPercentChange24h,
                Price::getPercentChange7d,
                Price::getPercentChange30d,
                Price::getPercentChange60d,
                Price::getPercentChange90d
        ).containsExactly(
                BTC_PRICE,
                BTC_PERCENT_1H,
                BTC_PERCENT_24H,
                BTC_PERCENT_7D,
                BTC_PERCENT_30D,
                BTC_PERCENT_60D,
                BTC_PERCENT_90D
        );

        assertThat(crypto.getPrice().getId())
                .isGreaterThan(0L);

        assertThat(crypto.getPrice().getLastUpdate())
                .isBefore(LocalDateTime.now());
    }

    @Test
    @Sql({"/delete_data.sql", "/insert_data.sql"})
    void whenGetCryptoByNotExistID_thenReturnStatus404NotFound() {
        var responseEntity = template.getForEntity("/api/v1/cryptocurrency/1234567890",
                JsonApiError.class);

        var crypto = Objects.requireNonNull(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(crypto).extracting(
                JsonApiError::getStatus,
                JsonApiError::getMessage
        ).containsExactly(
                HttpStatus.NOT_FOUND,
                "Unable to find cryptocurrency with id: 1234567890"
        );
    }
}
