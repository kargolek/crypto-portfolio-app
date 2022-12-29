package pl.cryptoportfolioapp.cryptopriceservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cryptoportfolioapp.cryptopriceservice.dto.CryptocurrencyDto;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.service.CryptocurrencyService;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Karol Kuta-Orlowicz
 */

@RestController
@RequestMapping(path = "api/v1/cryptocurrency")
@Slf4j
public class CryptocurrencyController {

    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @GetMapping("/{id}")
    public ResponseEntity<Cryptocurrency> getCryptocurrencyById(@PathVariable("id") Long id) {
        return null;
    }

    @GetMapping("")
    public List<Cryptocurrency> getCryptocurrencies(@RequestParam(name = "name", required = false) List<String> name) {
       return null;
    }


    @PostMapping("")
    public void postCryptocurrency(@Valid @RequestBody CryptocurrencyDto cryptocurrencyDto) {

    }

    @DeleteMapping("/{id}")
    public void deleteCryptocurrencyById(@PathVariable("id") Long id) {

    }

    @PutMapping("/{id}")
    public Cryptocurrency updateCryptocurrency(@PathVariable("id") Long id,
                                               @Valid @RequestBody CryptocurrencyDto cryptocurrencyDto) {
        return null;
    }

}
