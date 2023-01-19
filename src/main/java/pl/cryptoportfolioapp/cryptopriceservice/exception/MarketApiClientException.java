package pl.cryptoportfolioapp.cryptopriceservice.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Karol Kuta-Orlowicz
 */
public class MarketApiClientException extends RuntimeException {
    public MarketApiClientException(HttpStatus httpStatus, String clientMessage, String serverMessage) {
        super(String.format("Status code: %d, clientMessage: %s, serverMessage: %s",
                httpStatus.value(),
                clientMessage,
                serverMessage));
    }
}
