package pl.cryptoportfolioapp.cryptopriceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "pl.cryptoportfolioapp.cryptopriceservice.repository")
public class CryptoPriceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoPriceServiceApplication.class, args);
    }

}
