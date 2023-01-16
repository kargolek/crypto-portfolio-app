package pl.cryptoportfolioapp.cryptopriceservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CryptocurrencyDTO {
    private Long id;
    private String name;
    private String symbol;
    private Long coinMarketId;
    private PriceDTO priceDTO;
}
