package pl.cryptoportfolioapp.cryptopriceservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.cryptoportfolioapp.cryptopriceservice.dto.CryptocurrencyDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.PriceDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.CryptocurrencyResponseDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.PriceResponseDTO;
import pl.cryptoportfolioapp.cryptopriceservice.mapper.util.CycleAvoidingMappingContext;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karol Kuta-Orlowicz
 */
class CryptocurrencyMapperTest {
    private static final String FIAT_CURRENCY_USD = "USD";
    private final CryptocurrencyMapper mapper = Mappers.getMapper(CryptocurrencyMapper.class);
    private Cryptocurrency cryptocurrency;
    private CryptocurrencyDTO cryptocurrencyDTO;
    private PriceDTO priceDTO;
    private CryptocurrencyResponseDTO cryptocurrencyResponseDTO;
    private PriceResponseDTO priceResponseDTO;

    @BeforeEach
    void setUp() {
        Price price = Price.builder()
                .id(1L)
                .priceCurrent(new BigDecimal("18000.54321"))
                .percentChange1h(new BigDecimal("0.5"))
                .build();

        cryptocurrency = Cryptocurrency.builder()
                .id(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(1L)
                .price(price)
                .build();
        price.setCryptocurrency(cryptocurrency);

        priceDTO = new PriceDTO()
                .setId(2L)
                .setPriceCurrent(new BigDecimal("2000.2000"))
                .setPercentChange1h(new BigDecimal("0.0001"));

        cryptocurrencyDTO = new CryptocurrencyDTO()
                .setId(2L)
                .setName("Ethereum")
                .setSymbol("ETH")
                .setCoinMarketId(1027L)
                .setPriceDTO(priceDTO);

        priceResponseDTO = new PriceResponseDTO()
                .setPriceCurrent(new BigDecimal("0.90"))
                .setPercentChange1h(new BigDecimal("1.5"));

        cryptocurrencyResponseDTO = new CryptocurrencyResponseDTO()
                .setName("MATIC")
                .setSymbol("MATIC")
                .setCoinMarketId(2045L)
                .setQuote(Map.of(FIAT_CURRENCY_USD, priceResponseDTO));
    }

    @Test
    void whenMapDto_thenReturnPriceEntity() {
        var entity = mapper.mapDtoToPriceEntity(priceDTO);

        assertThat(entity).extracting(
                Price::getId,
                Price::getPriceCurrent,
                Price::getPercentChange1h
        ).containsExactly(
                priceDTO.getId(),
                priceDTO.getPriceCurrent(),
                priceDTO.getPercentChange1h()
        );
    }

    @Test
    void whenMapResponse_thenReturnPriceDTO() {
        var dto = mapper.mapResponseToPriceDto(priceResponseDTO);

        assertThat(dto).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h
        ).containsExactly(
                null,
                cryptocurrencyResponseDTO.getQuote().get(FIAT_CURRENCY_USD).getPriceCurrent(),
                cryptocurrencyResponseDTO.getQuote().get(FIAT_CURRENCY_USD).getPercentChange1h()
        );
    }

    @Test
    void whenMapEntity_thenReturnCryptocurrencyDTO() {
        var dto = mapper.mapEntityToCryptocurrencyDto(cryptocurrency, new CycleAvoidingMappingContext());

        assertThat(dto).extracting(
                CryptocurrencyDTO::getId,
                CryptocurrencyDTO::getName,
                CryptocurrencyDTO::getSymbol,
                CryptocurrencyDTO::getCoinMarketId
        ).containsExactly(
                cryptocurrency.getId(),
                cryptocurrency.getName(),
                cryptocurrency.getSymbol(),
                cryptocurrency.getCoinMarketId()
        );

        assertThat(dto.getPriceDTO())
                .extracting(
                        PriceDTO::getId,
                        PriceDTO::getPriceCurrent,
                        PriceDTO::getPercentChange1h
                ).containsExactly(
                        cryptocurrency.getPrice().getId(),
                        cryptocurrency.getPrice().getPriceCurrent(),
                        cryptocurrency.getPrice().getPercentChange1h()
                );
    }

    @Test
    void whenMapResponse_thenReturnCryptocurrencyDto() {
        var dto = mapper.mapResponseToCryptocurrencyDto(cryptocurrencyResponseDTO);

        assertThat(dto).extracting(
                CryptocurrencyDTO::getId,
                CryptocurrencyDTO::getName,
                CryptocurrencyDTO::getSymbol,
                CryptocurrencyDTO::getCoinMarketId
        ).containsExactly(
                null,
                cryptocurrencyResponseDTO.getName(),
                cryptocurrencyResponseDTO.getSymbol(),
                cryptocurrencyResponseDTO.getCoinMarketId()
        );

        assertThat(dto.getPriceDTO()).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h
        ).containsExactly(
                null,
                cryptocurrencyResponseDTO.getQuote().get(FIAT_CURRENCY_USD).getPriceCurrent(),
                cryptocurrencyResponseDTO.getQuote().get(FIAT_CURRENCY_USD).getPercentChange1h()
        );
    }

    @Test
    void whenUpdateDtoByResponse_thenReturnUpdatedPriceDto() {
        var dto = mapper.updateDtoByPriceResDto(priceDTO, priceResponseDTO);

        assertThat(dto).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h
        ).containsExactly(
                priceDTO.getId(),
                cryptocurrencyResponseDTO.getQuote().get(FIAT_CURRENCY_USD).getPriceCurrent(),
                cryptocurrencyResponseDTO.getQuote().get(FIAT_CURRENCY_USD).getPercentChange1h()
        );
    }

    @Test
    void whenUpdateDtoByResponse_thenReturnUpdatedCryptocurrencyDto() {
        var dtoUpdated = mapper.updateDtoByCryptocurrencyResDto(cryptocurrencyDTO,
                cryptocurrencyResponseDTO);

        assertThat(dtoUpdated).extracting(
                CryptocurrencyDTO::getId,
                CryptocurrencyDTO::getName,
                CryptocurrencyDTO::getSymbol,
                CryptocurrencyDTO::getCoinMarketId
        ).containsExactly(
                cryptocurrencyDTO.getId(),
                cryptocurrencyDTO.getName(),
                cryptocurrencyDTO.getSymbol(),
                cryptocurrencyDTO.getCoinMarketId()
        );

        assertThat(dtoUpdated.getPriceDTO()).extracting(
                PriceDTO::getId,
                PriceDTO::getPriceCurrent,
                PriceDTO::getPercentChange1h
        ).containsExactly(
                cryptocurrencyDTO.getPriceDTO().getId(),
                cryptocurrencyResponseDTO.getPriceResponseDTO().getPriceCurrent(),
                cryptocurrencyResponseDTO.getPriceResponseDTO().getPercentChange1h()
        );
    }
}