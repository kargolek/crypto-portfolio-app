package pl.cryptoportfolioapp.cryptopriceservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.cryptoportfolioapp.cryptopriceservice.dto.CryptocurrencyDTO;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.service.CryptocurrencyService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author Karol Kuta-Orlowicz
 */

@RequestMapping(path = "api/v1/cryptocurrency")
@RestController
public class CryptocurrencyController {

    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @GetMapping("/{id}")
    public ResponseEntity<Cryptocurrency> getCryptocurrencyById(@PathVariable("id") Long id) {
        var cryptocurrency = cryptocurrencyService.getById(id);
        return ResponseEntity.ok(cryptocurrency);
    }

    @GetMapping("")
    public List<Cryptocurrency> getCryptocurrencies(@RequestParam(name = "name", required = false) List<String> names) {
        return Optional.ofNullable(names).isPresent() ?
                cryptocurrencyService.getByName(names) :
                cryptocurrencyService.getCryptocurrencies();
    }

    @PostMapping("")
    public ResponseEntity<Cryptocurrency> registerCryptocurrency(
            @Valid @RequestBody CryptocurrencyDTO cryptocurrencyDTO) {
        var registerCryptocurrency = cryptocurrencyDTO.toCryptocurrency();
        var cryptocurrencyEntity = cryptocurrencyService.addCryptocurrency(registerCryptocurrency);
        return ResponseEntity.status(HttpStatus.CREATED).body(cryptocurrencyEntity);
    }

    @DeleteMapping("/{id}")
    public void deleteCryptocurrencyById(@PathVariable("id") Long id) {
        cryptocurrencyService.deleteCryptocurrency(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cryptocurrency> updateCryptocurrency(@PathVariable("id") Long id,
                                                               @Valid @RequestBody CryptocurrencyDTO cryptocurrencyDTO) {
        var cryptocurrency = cryptocurrencyDTO.toCryptocurrency();
        var body = cryptocurrencyService.updateCryptocurrency(id, cryptocurrency);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
