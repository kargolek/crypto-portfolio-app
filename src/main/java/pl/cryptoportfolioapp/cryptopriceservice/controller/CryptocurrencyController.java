package pl.cryptoportfolioapp.cryptopriceservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.service.CryptocurrencyService;

import java.util.List;

/**
 * @author Karol Kuta-Orlowicz
 */

@RestController
@RequestMapping(path = "api/v1/cryptocurrency")
public class CryptocurrencyController {

    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @PostMapping("")
    public void postCryptocurrency(@RequestBody Cryptocurrency cryptocurrency){

    }

    @DeleteMapping("/{id}")
    public void deleteCryptocurrencyById(@PathVariable("id") Long id){

    }

    @PostMapping("/{id}")
    public void updateCryptocurrency(@PathVariable("id") Long id,
                                     @RequestBody Cryptocurrency cryptocurrency){

    }

    @GetMapping("")
    public List<Cryptocurrency> getCryptocurrencies(){
        return null;
    }

    @GetMapping("/{id}")
    public Cryptocurrency getCryptocurrencyById(@PathVariable("id") Long id){
        return null;
    }

    @GetMapping("")
    public List<Cryptocurrency> getCryptocurrencyByName(@RequestParam("name") String name){
        return null;
    }

}
