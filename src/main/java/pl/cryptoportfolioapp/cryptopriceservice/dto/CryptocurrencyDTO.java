package pl.cryptoportfolioapp.cryptopriceservice.dto;

import lombok.Builder;
import lombok.Data;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class CryptocurrencyDTO {
    @NotEmpty(message = "Name cannot be an empty")
    @Size(min = 2, max = 100, message = "Name length exceeds range [2,100]")
    private String name;

    @NotEmpty(message = "Symbol cannot be an empty")
    @Size(min = 2, max = 20, message = "Symbol length exceeds range [2,20]")
    private String symbol;

    @NotNull(message = "Coin market ID cannot be a null")
    @Min(value = 1, message = "Coin market ID must be greater than 0")
    private Long coinMarketId;

    public Cryptocurrency toCryptocurrency() {
        return Cryptocurrency.builder()
                .name(name)
                .symbol(symbol)
                .coinMarketId(coinMarketId)
                .build();
    }
}
