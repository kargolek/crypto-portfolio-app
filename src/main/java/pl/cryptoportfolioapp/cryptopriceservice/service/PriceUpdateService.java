package pl.cryptoportfolioapp.cryptopriceservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.cryptoportfolioapp.cryptopriceservice.dto.client.CryptocurrencyQuoteDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.model.CryptocurrencyDTO;
import pl.cryptoportfolioapp.cryptopriceservice.mapper.CryptocurrencyMapper;
import pl.cryptoportfolioapp.cryptopriceservice.mapper.util.CycleAvoidingMappingContext;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Karol Kuta-Orlowicz
 */
@Service
@Slf4j
public class PriceUpdateService {

    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private MarketApiClientService marketApiClientService;

    public List<Price> updateCryptocurrencyPrices() {
        var cryptocurrencies = cryptocurrencyService.getCryptocurrencies();
        if (cryptocurrencies.isEmpty())
            return Collections.emptyList();
        var cryptocurrencyDTOS = cryptocurrencies.stream()
                .map((Cryptocurrency cryptocurrency) ->
                        CryptocurrencyMapper.INSTANCE.mapEntityToCryptocurrencyDto(cryptocurrency,
                                new CycleAvoidingMappingContext()))
                .toList();

        var ids = createCryptocurrenciesIds(cryptocurrencyDTOS);

        var cryptocurrencyQuoteDTOS = marketApiClientService.getLatestPriceByIds(ids)
                .orElseThrow()
                .getData()
                .values();

        var prices = updateDtoByNewPrice(cryptocurrencyDTOS, cryptocurrencyQuoteDTOS)
                .stream()
                .map(CryptocurrencyMapper.INSTANCE::mapDtoToCryptocurrencyEntity)
                .map(Cryptocurrency::getPrice)
                .filter(price -> price != null && price.getPriceCurrent() != null)
                .toList();
        priceService.updatePrices(prices);
        return prices;
    }

    private List<CryptocurrencyDTO> updateDtoByNewPrice(List<CryptocurrencyDTO> cryptocurrencyDTOS,
                                                        Collection<CryptocurrencyQuoteDTO> cryptocurrencyQuoteDTOS) {
        return cryptocurrencyDTOS.stream()
                .<CryptocurrencyDTO>mapMulti((cryptocurrencyDTO, consumer) -> {
                    cryptocurrencyQuoteDTOS.stream()
                            .filter(cryptocurrencyQuoteDTO -> cryptocurrencyQuoteDTO.getCoinMarketId() != null)
                            .filter(cryptocurrencyQuoteDTO ->
                                    cryptocurrencyQuoteDTO.getCoinMarketId().equals(cryptocurrencyDTO.getCoinMarketId()))
                            .findFirst()
                            .ifPresent(cryptocurrencyQuoteDTO ->
                                    CryptocurrencyMapper.INSTANCE.updateDtoByCryptocurrencyQuoteDto(cryptocurrencyDTO, cryptocurrencyQuoteDTO));
                    consumer.accept(cryptocurrencyDTO);
                })
                .collect(Collectors.toList());
    }

    private String createCryptocurrenciesIds(List<CryptocurrencyDTO> cryptocurrencyDTOS) {
        return cryptocurrencyDTOS
                .stream()
                .map(CryptocurrencyDTO::getCoinMarketId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

}
