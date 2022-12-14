package pl.cryptoportfolioapp.cryptopriceservice.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "Cryptocurrency")
@Table(name = "cryptocurrency", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueCoinMarketCapId", columnNames = "coin_market_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Cryptocurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "coin_market_id", nullable = false)
    private Long coinMarketId;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

}
