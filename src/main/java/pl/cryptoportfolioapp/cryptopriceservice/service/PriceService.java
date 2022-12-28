package pl.cryptoportfolioapp.cryptopriceservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.exception.PriceNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;
import pl.cryptoportfolioapp.cryptopriceservice.repository.PriceRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Karol Kuta-Orlowicz
 */
@Service
@Slf4j
@AllArgsConstructor
public class PriceService {

    private PriceRepository priceRepository;

    public Price update(Price price){
        var id = price.getId();
        var priceToUpdate = priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException(id));
        price.setLastUpdate(LocalDateTime.now(ZoneOffset.UTC));
        log.info(String.format("Updating cryptocurrency price old price: %f, new price: %f cryptocurrency: %s",
                priceToUpdate.getPriceCurrent(),
                price.getPriceCurrent(),
                price.getCryptocurrency().getName()));
        return priceRepository.save(price);
    }
}
