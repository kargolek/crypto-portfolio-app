package pl.cryptoportfolioapp.cryptopriceservice.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import pl.cryptoportfolioapp.cryptopriceservice.dto.client.MapDataDTO;
import pl.cryptoportfolioapp.cryptopriceservice.dto.client.QuotesDataDTO;
import pl.cryptoportfolioapp.cryptopriceservice.service.impl.MarketApiClientImpl;

import java.util.Optional;

/**
 * @author Karol Kuta-Orlowicz
 */
@Service
@Slf4j
@NoArgsConstructor
public class MarketApiClientService {

    @Autowired
    private MarketApiClient marketApiClient;

    @Value("${api.coin.market.cap.quote.endpoint}")
    private String quotesLatestEndpoint;

    @Value("${api.coin.market.cap.map.endpoint}")
    private String mapCryptocurrencyEndpoint;


    public MarketApiClientService(WebClient webClient) {
        this.marketApiClient = new MarketApiClientImpl(webClient);
    }

    public Optional<QuotesDataDTO> getLatestPriceByIds(String ids) {
        var uri = UriComponentsBuilder.newInstance()
                .path(this.quotesLatestEndpoint)
                .queryParamIfPresent("id", Optional.of(ids))
                .build()
                .toUri();
        return marketApiClient.getRequest(uri, QuotesDataDTO.class);
    }

    public Optional<MapDataDTO> getCryptoMarketIdBySymbol(String symbol){
        var uri = UriComponentsBuilder.newInstance()
                .path(this.mapCryptocurrencyEndpoint)
                .queryParamIfPresent("symbol", Optional.of(symbol))
                .build()
                .toUri();
        return marketApiClient.getRequest(uri, MapDataDTO.class);
    }
}
