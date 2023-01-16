package pl.cryptoportfolioapp.cryptopriceservice.mapper.util;

import org.mapstruct.Qualifier;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.PriceResponseDTO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * @author Karol Kuta-Orlowicz
 */
public class MappingUtil {
    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface PriceMap {
    }

    @PriceMap
    public PriceResponseDTO getPriceResponseDTO(Map<String, PriceResponseDTO> quote) {
        return quote.get("USD");
    }
}

