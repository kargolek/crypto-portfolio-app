package pl.cryptoportfolioapp.cryptopriceservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.cryptoportfolioapp.cryptopriceservice.dto.controller.CryptocurrencyPostDTO;
import pl.cryptoportfolioapp.cryptopriceservice.exception.CryptocurrencyNotFoundException;
import pl.cryptoportfolioapp.cryptopriceservice.exception.MarketApiClientException;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;
import pl.cryptoportfolioapp.cryptopriceservice.repository.CryptocurrencyRepository;
import pl.cryptoportfolioapp.cryptopriceservice.service.CryptocurrencyService;
import pl.cryptoportfolioapp.cryptopriceservice.service.MarketApiClientService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Karol Kuta-Orlowicz
 */
@WebMvcTest(CryptocurrencyController.class)
@Tag("UnitTest")
class CryptocurrencyControllerUnitTest {

    private final String path = "/api/v1/cryptocurrency";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CryptocurrencyService cryptocurrencyService;

    @MockBean
    private CryptocurrencyRepository cryptocurrencyRepository;

    @MockBean
    private MarketApiClientService marketApiClientService;

    @Autowired
    private ObjectMapper objectMapper;
    private Cryptocurrency cryptocurrencyBTC;
    private Cryptocurrency cryptocurrencyETH;

    @BeforeEach
    void setUp() {
        cryptocurrencyBTC = Cryptocurrency.builder()
                .id(1L)
                .name("Bitcoin")
                .symbol("BTC")
                .coinMarketId(1L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        var priceBTC = Price.builder()
                .id(1L)
                .priceCurrent(new BigDecimal("10500.001"))
                .percentChange1h(new BigDecimal("0.01"))
                .cryptocurrency(cryptocurrencyBTC)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        cryptocurrencyBTC.setPrice(priceBTC);

        cryptocurrencyETH = Cryptocurrency.builder()
                .id(2L)
                .name("Ethereum")
                .symbol("ETH")
                .coinMarketId(2L)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        var priceETH = Price.builder()
                .id(1L)
                .priceCurrent(new BigDecimal("500.001"))
                .percentChange1h(new BigDecimal("0.02"))
                .cryptocurrency(cryptocurrencyETH)
                .lastUpdate(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        cryptocurrencyETH.setPrice(priceETH);
    }

    @Test
    void whenGetCryptocurrencyById_thenReturn200JsonCryptocurrencyData() throws Exception {
        when(cryptocurrencyService.getById(any()))
                .thenReturn(cryptocurrencyBTC);

        mockMvc.perform(MockMvcRequestBuilders.get(path + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Bitcoin"))
                .andExpect(jsonPath("$.symbol").value("BTC"))
                .andExpect(jsonPath("$.coinMarketId").value(1L))
                .andExpect(jsonPath("$.price.priceCurrent").isNumber());
    }

    @Test
    void whenGetCryptocurrencyByIdNotExisted_thenReturn404JsonError() throws Exception {
        when(cryptocurrencyService.getById(any()))
                .thenThrow(new CryptocurrencyNotFoundException(1L));

        mockMvc.perform(MockMvcRequestBuilders.get(path + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Unable to find cryptocurrency with id: 1"));
    }

    @Test
    void whenGetCryptocurrencies_thenReturn200JsonListCryptocurrenciesData() throws Exception {
        when(cryptocurrencyService.getCryptocurrencies())
                .thenReturn(Collections.singletonList(cryptocurrencyBTC));

        mockMvc.perform(MockMvcRequestBuilders.get(path))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void whenGetCryptosByQueryName_thenReturn200JsonCryptocurrencyData() throws Exception {
        when(cryptocurrencyService.getByName(List.of("Bitcoin", "Ethereum")))
                .thenReturn(List.of(cryptocurrencyBTC, cryptocurrencyETH));

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=Bitcoin,Ethereum"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name").value("Bitcoin"))
                .andExpect(jsonPath("[0].symbol").value("BTC"))
                .andExpect(jsonPath("[0].coinMarketId").value(1L))
                .andExpect(jsonPath("[0].price").isNotEmpty())
                .andExpect(jsonPath("[1].name").value("Ethereum"))
                .andExpect(jsonPath("[1].symbol").value("ETH"))
                .andExpect(jsonPath("[1].coinMarketId").value(2L))
                .andExpect(jsonPath("[1].price").isNotEmpty());
    }

    @Test
    void whenGetCryptocurrenciesByQueryTwoNameWhenNoExist_thenReturn200AndEmptyJson() throws Exception {
        when(cryptocurrencyService.getByName(List.of("Bitcoin", "Ethereum")))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=Bitcoin,Ethereum"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void whenGetCryptocurrenciesByQueryTwoNameWhenOnlyOneExist_thenReturnCryptoExistedAnd200() throws Exception {
        when(cryptocurrencyService.getByName(List.of("Bitcoin", "Ethereum")))
                .thenReturn(List.of(cryptocurrencyETH));

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=Bitcoin,Ethereum"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name").value("Ethereum"))
                .andExpect(jsonPath("[0].symbol").value("ETH"))
                .andExpect(jsonPath("[0].coinMarketId").value(2L))
                .andExpect(jsonPath("[0].price").isNotEmpty());
    }

    @Test
    void whenPostCryptocurrency_thenReturnStatus201AndBodyData() throws Exception {
        when(cryptocurrencyService.addCryptocurrency(any(Cryptocurrency.class)))
                .thenReturn(cryptocurrencyBTC);

        var body = new CryptocurrencyPostDTO()
                .setName("Bitcoin")
                .setSymbol("BTC")
                .setCoinMarketId(1L);

        mockMvc.perform(MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bitcoin"))
                .andExpect(jsonPath("$.symbol").value("BTC"))
                .andExpect(jsonPath("$.coinMarketId").value(1L))
                .andExpect(jsonPath("$.price").isNotEmpty());
    }

    @Test
    void whenPostCryptocurrencyNoMarketId_thenReturnStatus201AndBodyData() throws Exception {
        when(cryptocurrencyService.addCryptocurrency(any(Cryptocurrency.class)))
                .thenReturn(cryptocurrencyBTC);

        var body = new CryptocurrencyPostDTO()
                .setName("Bitcoin")
                .setSymbol("BTC");

        mockMvc.perform(MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bitcoin"))
                .andExpect(jsonPath("$.symbol").value("BTC"))
                .andExpect(jsonPath("$.coinMarketId").value(1L))
                .andExpect(jsonPath("$.price").isNotEmpty());
    }

    @Test
    void whenPostCryptocurrencyNoMarketIdIncorrectSymbol_thenReturnStatus400AndBodyData() throws Exception {
        when(cryptocurrencyService.addCryptocurrency(any(Cryptocurrency.class)))
                .thenThrow(new MarketApiClientException(HttpStatus.BAD_REQUEST, "Error client occurred", "Invalid value for symbol: SOME_COIN"));

        var body = new CryptocurrencyPostDTO()
                .setName("SOME_COIN")
                .setSymbol("SOME_COIN");

        mockMvc.perform(MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void whenPostCryptocurrencyWithNoBodyData_thenReturn400AndJsonWithDetails() throws Exception {
        var cryptoDTO = new Cryptocurrency();

        mockMvc.perform(MockMvcRequestBuilders.post(path)
                        .content(objectMapper.writeValueAsString(cryptoDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void whenDeleteCryptocurrencyById_thenRepoDeleteByIdMethodPerform() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(path + "/1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(cryptocurrencyService).deleteCryptocurrency(1L);
    }

    @Test
    void whenDeleteCryptocurrencyByIdNotExisted_thenReturn404JsonError() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(cryptocurrencyService).deleteCryptocurrency(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete(path + "/2"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));

        verify(cryptocurrencyService).deleteCryptocurrency(2L);
    }

    @Test
    void whenUpdateCryptocurrency_thenReturn200JsonUpdatedCryptocurrencyData() throws Exception {
        when(cryptocurrencyService.updateCryptocurrency(anyLong(), any(Cryptocurrency.class)))
                .thenReturn(cryptocurrencyBTC);

        var body = new CryptocurrencyPostDTO()
                .setName("Bitcoin")
                .setSymbol("BTC")
                .setCoinMarketId(1L);

        mockMvc.perform(MockMvcRequestBuilders.put(path + "/" + cryptocurrencyBTC.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void whenUpdateCryptocurrencyWhenIdNotExist_thenReturn404JsonError() throws Exception {
        when(cryptocurrencyService.updateCryptocurrency(anyLong(), any(Cryptocurrency.class)))
                .thenThrow(new CryptocurrencyNotFoundException(1L));

        var body = new CryptocurrencyPostDTO()
                .setName("Bitcoin")
                .setSymbol("BTC")
                .setCoinMarketId(1L);

        mockMvc.perform(MockMvcRequestBuilders.put(path + "/" + cryptocurrencyBTC.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }
}