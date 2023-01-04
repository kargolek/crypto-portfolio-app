package pl.cryptoportfolioapp.cryptopriceservice.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({"pl.cryptoportfolioapp.cryptopriceservice"})
@EntityScan(basePackages = "pl.cryptoportfolioapp.cryptopriceservice")
@EnableJpaRepositories(basePackages = "pl.cryptoportfolioapp.cryptopriceservice")
public class DataConfig {
}
