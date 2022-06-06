package com.currency.currencyrate;

import com.currency.currencyrate.client.CurrencyClient;
import com.currency.currencyrate.client.GifClient;
import com.currency.currencyrate.controller.CurrencyController;
import com.currency.currencyrate.model.Currency;
import com.currency.currencyrate.model.Gif;
import com.currency.currencyrate.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDate.now;
import static java.time.ZoneId.of;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest
class CurrencyRateApplicationTests {

    @Value("${api.currency.zoneId}")
    private String zoneId;

    private LocalDate yesterday;

    @Value("${api.currency.appId}")
    private String currencyAppId;

    @Value("${api.gif.appId}")
    private String gifAppId;

    @Value("${api.gif.currency-difference-up}")
    private String upTagWord;

    @Value("${api.gif.currency-difference-down}")
    private String downTagWord;

    @MockBean
    private GifClient gifClient;

    @MockBean
    private CurrencyClient currencyClient;

    CurrencyController currencyController;

    CurrencyService currencyService;

    @Autowired
    public CurrencyRateApplicationTests(GifClient gifClient, CurrencyClient currencyClient, CurrencyController currencyController, CurrencyService currencyService) {
        this.gifClient = gifClient;
        this.currencyClient = currencyClient;
        this.currencyController = currencyController;
        this.currencyService = currencyService;
    }

    @BeforeEach
    public void setUp() {
        yesterday = now(of(zoneId)).minus(1, DAYS);

        setField(currencyService, "baseCurrencyCode", "BAS");
        setField(currencyController, "baseCurrencyCode", "BAS");
    }

    @Test
    void contextLoads() {
        assertNotNull(currencyController);
    }

    @Test
    void givenTargetCurrency_whenCurrencyDifferenceDown_thenReturnsBrokeGif() {
        Currency yesterdayRate = createCurrencyWithRates("BAS", 1.5, "TAR", 1.3);
        Currency todayRate = createCurrencyWithRates("BAS", 1.3, "TAR", 1.5);

        when(currencyClient.getLatest(currencyAppId)).thenReturn(todayRate);
        when(currencyClient.getByDate(yesterday, currencyAppId)).thenReturn(yesterdayRate);

        Gif returnedGif = new Gif(URI.create("test.com"));
        byte[] gifByteArray = new byte[9];

        when(gifClient.getRandomGifByTag(gifAppId, downTagWord)).thenReturn(returnedGif);
        when(gifClient.getGifByUrl(URI.create("test.com"))).thenReturn(gifByteArray);

        assertEquals(gifByteArray, currencyController.getGifByCurrencyDifference("TAR"));
    }

    @Test
    void givenTargetCurrency_whenCurrencyDifferenceUp_thenReturnsRichGif() {
        Currency yesterdayRate = createCurrencyWithRates("BAS", 1.3, "TAR", 1.5);
        Currency todayRate = createCurrencyWithRates("BAS", 1.5, "TAR", 1.3);

        when(currencyClient.getLatest(currencyAppId)).thenReturn(todayRate);
        when(currencyClient.getByDate(yesterday, currencyAppId)).thenReturn(yesterdayRate);

        Gif returnedGif = new Gif(URI.create("test.com"));
        byte[] gifByteArray = new byte[9];

        when(gifClient.getRandomGifByTag(gifAppId, upTagWord)).thenReturn(returnedGif);
        when(gifClient.getGifByUrl(URI.create("test.com"))).thenReturn(gifByteArray);

        assertEquals(gifByteArray, currencyController.getGifByCurrencyDifference("TAR"));
    }

    private Currency createCurrencyWithRates(String baseCurrencyCode, double valueBaseCode, String targetCurrencyCode, double valueTargetCode) {
        Map<String, Double> rates = new HashMap<>();
        rates.put(baseCurrencyCode, valueBaseCode);
        rates.put(targetCurrencyCode, valueTargetCode);
        Currency currency = new Currency();
        currency.setRates(rates);
        return currency;
    }
}