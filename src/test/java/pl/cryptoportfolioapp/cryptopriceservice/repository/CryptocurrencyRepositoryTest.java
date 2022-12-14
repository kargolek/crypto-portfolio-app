package pl.cryptoportfolioapp.cryptopriceservice.repository;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.cryptoportfolioapp.cryptopriceservice.container.MySqlTestContainer;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CryptocurrencyRepositoryTest extends MySqlTestContainer {

    @Autowired
    private CryptocurrencyRepository cryptoRepository;

    private Cryptocurrency cryptocurrency;

    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSaveEntity() {
        //when
        cryptoRepository.save(cryptocurrency);
        //then
        var result = cryptoRepository.getReferenceById(cryptocurrency.getId());
        assertThat(result).isNotNull();
    }

    @Test
    void shouldReturnEntityById() {
        //given
        cryptoRepository.save(cryptocurrency);
        //when
        var result = cryptoRepository.findById(cryptocurrency.getId());
        //then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void shouldDeleteEntity() {
        //given
        cryptoRepository.save(cryptocurrency);
        //when
        cryptoRepository.delete(cryptocurrency);
        //then
        assertThat(cryptoRepository.findById(cryptocurrency.getId()).isPresent())
                .isFalse();
    }

    @Test
    void shouldThrowConstraintViolationExceptionForUniqueCoinMarketCapId() {
        //given
        cryptoRepository.save(cryptocurrency);
        var underTest = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(cryptocurrency.getCoinMarketId())
                .lastUpdate(LocalDateTime.now())
                .build();
        //when then
        assertThatThrownBy(() -> cryptoRepository.save(underTest))
                .hasCauseInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("cryptocurrency.UniqueCoinMarketCapId");
    }


}