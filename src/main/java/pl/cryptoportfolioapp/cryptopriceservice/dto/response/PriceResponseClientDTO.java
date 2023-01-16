package pl.cryptoportfolioapp.cryptopriceservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class PriceResponseClientDTO {

    @JsonProperty("data")
    private Map<String, CryptocurrencyResponseDTO> data = new HashMap<>();

    @JsonProperty("status")
    private ResponseStatusDTO status;
}
