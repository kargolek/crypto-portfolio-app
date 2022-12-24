package pl.cryptoportfolioapp.cryptopriceservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CryptocurrencyService {

    private final CryptocurrencyRepository cryptocurrencyRepository;

    public Cryptocurrency addCryptocurrency(Cryptocurrency cryptocurrency) {
        log.info(String.format("Add new cryptocurrency name: %s, coinMarketCapId: %s",
                cryptocurrency.getName(),
                cryptocurrency.getCoinMarketId()));
        cryptocurrency.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));
        return cryptocurrencyRepository.save(cryptocurrency);
    }

    public List<Cryptocurrency> getCryptocurrencies() {
        log.info("Get all cryptocurrencies from DB");
        return cryptocurrencyRepository.findAll();
    }

    public Cryptocurrency getById(Long id) {
        log.info(String.format("Get cryptocurrency by id: %d", id));
        return cryptocurrencyRepository.findById(id)
                .orElseThrow(() -> new CryptocurrencyNotFoundException(id));
    }

    public Cryptocurrency updateCryptocurrency(Long id, Cryptocurrency cryptocurrency) {
        var cryptoUpdateId = cryptocurrencyRepository.findById(id)
                .orElseThrow(() -> new CryptocurrencyNotFoundException(id)).getId();
        cryptocurrency.setId(cryptoUpdateId);
        cryptocurrency.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));
        log.info(String.format("Update cryptocurrency with id: %d, new name: %s, symbol: %s, coinMarketId: %d",
                cryptoUpdateId,
                cryptocurrency.getName(),
                cryptocurrency.getSymbol(),
                cryptocurrency.getCoinMarketId()));
        return cryptocurrencyRepository.save(cryptocurrency);
    }

    public void deleteCryptocurrency(Long id) {
        log.info(String.format("Delete cryptocurrency id: %d", id));
        cryptocurrencyRepository.deleteById(id);
    }

    public Cryptocurrency getByName(String name) {
        return cryptocurrencyRepository.findByName(name)
                .orElseThrow(() -> new CryptocurrencyNotFoundException(name));
    }

}
