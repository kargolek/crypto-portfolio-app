package pl.cryptoportfolioapp.cryptopriceservice.exception;

/**
 * @author Karol Kuta-Orlowicz
 */
public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(String message) {
        super(message);
    }
}
