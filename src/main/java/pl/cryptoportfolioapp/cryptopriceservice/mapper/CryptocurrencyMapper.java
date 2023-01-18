package pl.cryptoportfolioapp.cryptopriceservice.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import pl.cryptoportfolioapp.cryptopriceservice.dto.CryptocurrencyDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.PriceDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.post.CryptocurrencyPostDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.CryptocurrencyResponseDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.PriceResponseDTO;
import pl.cryptoportfolioapp.cryptopriceservice.mapper.util.CycleAvoidingMappingContext;
import pl.cryptoportfolioapp.cryptopriceservice.mapper.util.MappingUtil;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;

/**
 * @author Karol Kuta-Orlowicz
 */

@Mapper(uses = MappingUtil.class)
public interface CryptocurrencyMapper {

    CryptocurrencyMapper INSTANCE = Mappers.getMapper(CryptocurrencyMapper.class);

    @Mapping(target = "cryptocurrency", source = "cryptocurrencyDTO")
    Price mapDtoToPriceEntity(PriceDTO priceDTO);

    @Mapping(target = "cryptocurrencyDTO", source = "cryptocurrency")
    PriceDTO mapEntityToPriceDto(Price price);

    @Mapping(target = "id", ignore = true)
    PriceDTO mapResponseToPriceDto(PriceResponseDTO priceResponseDTO);

    @Mapping(target = "priceDTO", source = "price")
    CryptocurrencyDTO mapEntityToCryptocurrencyDto(Cryptocurrency cryptocurrency,
                                                   @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "price", source = "priceDTO")
    Cryptocurrency mapDtoToCryptocurrencyEntity(CryptocurrencyDTO cryptocurrencyDTO);

    @Mappings({
            @Mapping(target = "priceDTO", source = "quote", qualifiedBy = MappingUtil.PriceMap.class)
    })
    CryptocurrencyDTO mapResponseToCryptocurrencyDto(CryptocurrencyResponseDTO cryptocurrencyResponseDTO);

    Cryptocurrency mapPostDtoToCryptocurrencyEntity(CryptocurrencyPostDTO cryptocurrencyPostDTO);

    CryptocurrencyPostDTO mapEntityToCryptocurrencyPostDto(Cryptocurrency cryptocurrency);

    @Mapping(target = "id", ignore = true)
    PriceDTO updateDtoByPriceResDto(@MappingTarget PriceDTO priceDTO, PriceResponseDTO priceResponseDTO);

    @Mappings({
            @Mapping(target = "priceDTO", source = "quote", qualifiedBy = MappingUtil.PriceMap.class),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", ignore = true),
            @Mapping(target = "symbol", ignore = true),
            @Mapping(target = "coinMarketId", ignore = true)
    })
    CryptocurrencyDTO updateDtoByCryptocurrencyResDto(@MappingTarget CryptocurrencyDTO cryptocurrencyDTO,
                                                      CryptocurrencyResponseDTO cryptocurrencyResponseDTO);
}