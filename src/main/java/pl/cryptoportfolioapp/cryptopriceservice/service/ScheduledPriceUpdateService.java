package pl.cryptoportfolioapp.cryptopriceservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.cryptoportfolioapp.cryptopriceservice.dto.CoinMarketDataDTO;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ScheduledPriceUpdateService {

    @Autowired
    private WebClient webClient;
    @Value("${api.coin.market.cap.quote.endpoint}")
    private String endpoint;

    public CoinMarketDataDTO getPriceByIds(String... ids) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(this.endpoint)
                        .queryParam("id", String.join(",", ids))
                        .build())
                .retrieve()
                .bodyToMono(CoinMarketDataDTO.class)
                .block();
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    private void updateCryptocurrencyPrice() {
        log.info("Job working");
        log.info("Job price: " + getPriceByIds("1").getData().get("1").getName());
    }

}
