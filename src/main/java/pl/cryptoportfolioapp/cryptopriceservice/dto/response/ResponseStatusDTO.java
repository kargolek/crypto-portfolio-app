package pl.cryptoportfolioapp.cryptopriceservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Karol Kuta-Orlowicz
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseStatusDTO {

    @JsonProperty("error_message")
    private String message;
}
