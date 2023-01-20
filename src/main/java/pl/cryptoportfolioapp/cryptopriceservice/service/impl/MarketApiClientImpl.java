package pl.cryptoportfolioapp.cryptopriceservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.cryptoportfolioapp.cryptopriceservice.dto.client.OnErrorClientDTO;
import pl.cryptoportfolioapp.cryptopriceservice.exception.MarketApiClientException;
import pl.cryptoportfolioapp.cryptopriceservice.service.MarketApiClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

/**
 * @author Karol Kuta-Orlowicz
 */
@NoArgsConstructor
@AllArgsConstructor
@Component
@Slf4j
public class MarketApiClientImpl implements MarketApiClient {

    @Autowired
    private WebClient webClient;

    @Override
    public <T> Optional<T> getRequest(URI uri, Class<T> map) {
        log.info("Calling get request for path:{}, query:{}", uri.getPath(), uri.getQuery());
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path(uri.getPath())
                        .query(uri.getQuery())
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(OnErrorClientDTO.class)
                        .flatMap(body -> Mono.error(new MarketApiClientException(
                                clientResponse.statusCode(),
                                "Error during calling get request",
                                Optional.ofNullable(body.getStatus().getMessage()).orElse("No message")
                        ))))
                .bodyToMono(map)
                .blockOptional();
    }
}
