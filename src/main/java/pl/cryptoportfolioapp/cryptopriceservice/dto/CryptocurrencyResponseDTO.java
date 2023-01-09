package pl.cryptoportfolioapp.cryptopriceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class CryptocurrencyResponseDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("id")
    private Long coinMarketId;

    @JsonProperty("quote")
    private Map<String, PriceResponseDTO> quote = new HashMap<>();
}
