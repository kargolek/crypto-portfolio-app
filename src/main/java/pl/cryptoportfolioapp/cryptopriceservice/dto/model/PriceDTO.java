package pl.cryptoportfolioapp.cryptopriceservice.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import pl.cryptoportfolioapp.cryptopriceservice.dto.model.CryptocurrencyDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Karol Kuta-Orlowicz
 */
@Data
@Accessors(chain = true)
public class PriceDTO {
    private Long id;
    private BigDecimal priceCurrent;
    private BigDecimal percentChange1h;
    private BigDecimal percentChange24h;
    private BigDecimal percentChange7d;
    private BigDecimal percentChange30d;
    private BigDecimal percentChange60d;
    private BigDecimal percentChange90d;
    private LocalDateTime lastUpdate;
    @JsonIgnore
    private CryptocurrencyDTO cryptocurrencyDTO;
}
