package pl.cryptoportfolioapp.cryptopriceservice.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "Price")
@Table(name = "price")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cryptocurrency_id", nullable = false)
    private Cryptocurrency cryptocurrencyId;

    @Column(name = "price_current", nullable = false)
    private BigDecimal priceCurrent;

    @Column(name = "percent_change_1h")
    private BigDecimal percentChange1h;

    @Column(name = "percent_change_24h")
    private BigDecimal percentChange24h;

    @Column(name = "percent_change_7d")
    private BigDecimal percentChange7d;

    @Column(name = "percent_change_30d")
    private BigDecimal percentChange30d;

    @Column(name = "percent_change_60d")
    private BigDecimal percentChange60d;

    @Column(name = "percent_change_90d")
    private BigDecimal percentChange90d;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
}
