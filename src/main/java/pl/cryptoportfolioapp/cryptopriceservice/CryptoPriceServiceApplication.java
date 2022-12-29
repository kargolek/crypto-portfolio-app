package pl.cryptoportfolioapp.cryptopriceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@SpringBootApplication
@Configuration
public class CryptoPriceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoPriceServiceApplication.class, args);
    }

}
