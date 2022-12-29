package pl.cryptoportfolioapp.cryptopriceservice.dto;

import lombok.Builder;
import lombok.Data;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;

import javax.validation.constraints.*;

@Data
@Builder
public class CryptocurrencyDto {

    @NotNull(message = "Cryptocurrency name can't be a null")
    @NotBlank(message = "Cryptocurrency name can't be a blank")
    @NotEmpty(message = "Cryptocurrency name can't be an empty")
    private String name;

    @NotNull(message = "Cryptocurrency name can't be a null")
    @NotBlank(message = "Cryptocurrency name can't be a blank")
    @NotEmpty(message = "Cryptocurrency name can't be an empty")
    private String symbol;

    @NotNull(message = "Cryptocurrency coin market ID can't be a null")
    @Min(value = 1, message = "Cryptocurrency coin market ID must have at least 1 number")
    @Positive(message = "Cryptocurrency coin market ID can't be negative number")
    private Long coinMarketId;

    public Cryptocurrency toCryptocurrency() {
        return Cryptocurrency.builder()
                .name(this.name)
                .symbol(this.symbol)
                .coinMarketId(this.coinMarketId)
                .build();
    }

}
