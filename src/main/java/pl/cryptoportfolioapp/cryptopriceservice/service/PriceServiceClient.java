package pl.cryptoportfolioapp.cryptopriceservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.cryptoportfolioapp.cryptopriceservice.dto.response.PriceResponseClientDTO;
import pl.cryptoportfolioapp.cryptopriceservice.exception.PriceServiceClientException;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @author Karol Kuta-Orlowicz
 */
@Service
@Slf4j
public class PriceServiceClient {

    private final WebClient webClient;

    @Value("${api.coin.market.cap.quote.endpoint}")
    private String quotesLatestEndpoint;

    public PriceServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<PriceResponseClientDTO> getLatestPriceByIds(String ids) {
        log.info(String.format("Calling latest cryptocurrency quotes for ids:%s", ids));
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(this.quotesLatestEndpoint)
                        .queryParam("id", ids)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    var httpStatus = clientResponse.statusCode();
                    return clientResponse.bodyToMono(PriceResponseClientDTO.class)
                            .flatMap(body -> Mono.error(new PriceServiceClientException(httpStatus,
                                    "Error call api latest quotes for ids: " + ids,
                                    body.getStatus().getMessage())));
                })
                .bodyToMono(PriceResponseClientDTO.class)
                .blockOptional();
    }
}
