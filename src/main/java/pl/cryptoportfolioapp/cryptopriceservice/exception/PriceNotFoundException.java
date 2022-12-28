package pl.cryptoportfolioapp.cryptopriceservice.exception;

/**
 * @author Karol Kuta-Orlowicz
 */
public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(Long id) {
        super(String.format("Unable to find price with id: %d", id));
    }
}
