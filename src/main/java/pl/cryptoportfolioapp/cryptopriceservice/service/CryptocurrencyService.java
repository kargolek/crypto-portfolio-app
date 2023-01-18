package pl.cryptoportfolioapp.cryptopriceservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;
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
        log.info(String.format("Adding new cryptocurrency name: %s, coinMarketCapId: %s",
                cryptocurrency.getName(),
                cryptocurrency.getCoinMarketId()));

        var price = Price.builder()
                .cryptocurrency(cryptocurrency)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        cryptocurrency.setPrice(price);
        cryptocurrency.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));
        return cryptocurrencyRepository.save(cryptocurrency);
    }

    public List<Cryptocurrency> getCryptocurrencies() {
        log.info("Finding all cryptocurrencies");
        return cryptocurrencyRepository.findAll();
    }

    public Cryptocurrency getById(Long id) {
        log.info(String.format("Finding cryptocurrency by id: %d", id));
        return cryptocurrencyRepository.findById(id)
                .orElseThrow(() -> new CryptocurrencyNotFoundException(id));
    }

    public Cryptocurrency updateCryptocurrency(Long id, Cryptocurrency cryptocurrency) {
        var cryptocurrencyToUpdate = cryptocurrencyRepository.findById(id)
                .orElseThrow(() -> new CryptocurrencyNotFoundException(id));

        cryptocurrencyToUpdate.setName(cryptocurrency.getName());
        cryptocurrencyToUpdate.setSymbol(cryptocurrency.getSymbol());
        cryptocurrencyToUpdate.setCoinMarketId(cryptocurrency.getCoinMarketId());
        cryptocurrencyToUpdate.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));

        log.info(String.format("Updating cryptocurrency with id: %d, new name: %s, symbol: %s, coinMarketId: %d",
                cryptocurrencyToUpdate.getId(),
                cryptocurrency.getName(),
                cryptocurrency.getSymbol(),
                cryptocurrency.getCoinMarketId()));
        return cryptocurrencyRepository.save(cryptocurrencyToUpdate);
    }

    public void deleteCryptocurrency(Long id) {
        log.info(String.format("Deleting cryptocurrency id: %d", id));
        cryptocurrencyRepository.deleteById(id);
    }

    public List<Cryptocurrency> getByName(List<String> name) {
        log.info(String.format("Finding cryptocurrencies by name: %s", name));
        return cryptocurrencyRepository.findByName(name);
    }
}
