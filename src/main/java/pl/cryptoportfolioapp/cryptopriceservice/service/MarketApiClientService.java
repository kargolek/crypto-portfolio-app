package pl.cryptoportfolioapp.cryptopriceservice.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import pl.cryptoportfolioapp.cryptopriceservice.dto.client.PriceResponseClientDTO;
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


    public MarketApiClientService(WebClient webClient) {
        this.marketApiClient = new MarketApiClientImpl(webClient);
    }

    public Optional<PriceResponseClientDTO> getLatestPriceByIds(String ids) {
        var uri = UriComponentsBuilder.newInstance()
                .path(this.quotesLatestEndpoint)
                .queryParamIfPresent("id", Optional.of(ids))
                .build()
                .toUri();
        log.info("Calling latest cryptocurrency quotes path:{}", uri);
        return marketApiClient.getRequest(uri, PriceResponseClientDTO.class);
    }
}
