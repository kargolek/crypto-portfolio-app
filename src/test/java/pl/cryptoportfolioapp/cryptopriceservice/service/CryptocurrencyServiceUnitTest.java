package pl.cryptoportfolioapp.cryptopriceservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptocurrencyServiceUnitTest {

    @Mock
    private CryptocurrencyRepository cryptocurrencyRepository;

    @InjectMocks
    private CryptocurrencyService underTestService;

    private Cryptocurrency cryptocurrency;


    @BeforeEach
    public void setup() {
        cryptocurrency = Cryptocurrency.builder()
                .id(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(RandomUtils.nextLong())
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSaveCryptocurrency() {
        when(cryptocurrencyRepository.save(cryptocurrency))
                .thenReturn(cryptocurrency);

        var expected = underTestService.addCryptocurrency(cryptocurrency);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate)
                .containsExactly(
                        cryptocurrency.getId(),
                        cryptocurrency.getName(),
                        cryptocurrency.getSymbol(),
                        cryptocurrency.getCoinMarketId(),
                        cryptocurrency.getLastUpdate()
                );
    }

    @Test
    void shouldReturnAllCryptocurrencies() {
        when(cryptocurrencyRepository.findAll())
                .thenReturn(Collections.singletonList(cryptocurrency));

        var expected = underTestService.getCryptocurrencies();

        assertThat(expected)
                .hasSize(1)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getCoinMarketId)
                .containsExactly(
                        tuple(1L,
                                "Bitcoin",
                                cryptocurrency.getCoinMarketId()
                        )
                );
    }

    @Test
    void shouldReturnCryptocurrencyById() {
        when(cryptocurrencyRepository.findById(1L))
                .thenReturn(Optional.of(cryptocurrency));

        var expected = underTestService.getById(1L);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate)
                .containsExactly(
                        cryptocurrency.getId(),
                        cryptocurrency.getName(),
                        cryptocurrency.getSymbol(),
                        cryptocurrency.getCoinMarketId(),
                        cryptocurrency.getLastUpdate()
                );
    }

    @Test
    void shouldThrowExceptionWhenNoCryptocurrencyFindById() {
        var id = 1L;
        when(cryptocurrencyRepository.findById(id))
                .thenThrow(new CryptocurrencyNotFoundException(id));

        assertThatThrownBy(() -> underTestService.getById(id))
                .hasMessage("Unable to find cryptocurrency with id: " + id)
                .isInstanceOf(CryptocurrencyNotFoundException.class);
    }

    @Test
    void shouldUpdateCryptocurrencyById() {
        var cryptoCurrencyUpdate = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(RandomUtils.nextLong())
                .build();
        when(cryptocurrencyRepository.findById(1L))
                .thenReturn(Optional.of(cryptocurrency));
        when(cryptocurrencyRepository.save(cryptoCurrencyUpdate))
                .thenReturn(cryptoCurrencyUpdate);

        var expected = underTestService.updateCryptocurrency(1L, cryptoCurrencyUpdate);

        assertThat(expected)
                .extracting(Cryptocurrency::getName,
                        Cryptocurrency::getSymbol)
                .containsExactly("Ethereum", "ETH");
    }

    @Test
    void shouldNotUpdateAndThrowException() {
        var cryptoCurrencyUpdate = Cryptocurrency.builder()
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(RandomUtils.nextLong())
                .lastUpdate(LocalDateTime.now())
                .build();

        assertThatThrownBy(() -> underTestService.updateCryptocurrency(1L, cryptoCurrencyUpdate));
    }

    @Test
    void shouldDeleteCryptocurrencyById() {
        var id = 1L;

        underTestService.deleteCryptocurrency(id);

        verify(cryptocurrencyRepository).deleteById(id);
    }

    @Test
    void shouldFindByName() {
        var cryptoName = "Bitcoin";
        when(cryptocurrencyRepository.findByName(cryptoName))
                .thenReturn(Optional.of(cryptocurrency));

        var expected = underTestService.getByName(cryptoName);

        assertThat(expected)
                .extracting(
                        Cryptocurrency::getId,
                        Cryptocurrency::getName,
                        Cryptocurrency::getSymbol,
                        Cryptocurrency::getCoinMarketId,
                        Cryptocurrency::getLastUpdate)
                .containsExactly(
                        cryptocurrency.getId(),
                        cryptocurrency.getName(),
                        cryptocurrency.getSymbol(),
                        cryptocurrency.getCoinMarketId(),
                        cryptocurrency.getLastUpdate()
                );
    }

    @Test
    void shouldNotFindByNameThrowException() {
        var cryptoName = "Ethereum";

        when(cryptocurrencyRepository.findByName(cryptoName))
                .thenThrow(new CryptocurrencyNotFoundException(cryptoName));

        assertThatThrownBy(() -> underTestService.getByName(cryptoName))
                .hasMessage("Unable to find cryptocurrency with name: " + cryptoName)
                .isInstanceOf(CryptocurrencyNotFoundException.class);
    }


}