package pl.cryptoportfolioapp.cryptopriceservice.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Karol Kuta-Orlowicz
 */
public class PriceServiceClientException extends RuntimeException {
    public PriceServiceClientException(HttpStatus httpStatus, String clientMessage, String serverMessage) {
        super(String.format("Status code: %d, clientMessage: %s, serverMessage: %s",
                httpStatus.value(),
                clientMessage,
                serverMessage));
    }
}
