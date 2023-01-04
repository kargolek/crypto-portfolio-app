package pl.cryptoportfolioapp.cryptopriceservice.container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class MySqlTestContainer {

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8");

    static {
        mySQLContainer.withReuse(true);
        mySQLContainer.start();
    }

    @DynamicPropertySource
    static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

}
