package pl.cryptoportfolioapp.cryptopriceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class CoinMarketDataDTO {

    @JsonProperty("data")
    private Map<String, CryptocurrencyResponseDTO> data = new HashMap<>();

}
