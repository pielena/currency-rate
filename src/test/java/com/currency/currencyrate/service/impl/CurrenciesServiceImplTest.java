package com.currency.currencyrate.service.impl;

import com.currency.currencyrate.client.CurrencyClient;
import com.currency.currencyrate.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.Map;

import static com.currency.currencyrate.service.CurrencyService.CurrencyDifference.DOWN;
import static com.currency.currencyrate.service.CurrencyService.CurrencyDifference.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest(classes = {CurrencyServiceImpl.class})
class CurrenciesServiceImplTest {

    @MockBean
    private CurrencyClient currencyClient;

    private final CurrencyServiceImpl currencyService;

    @Autowired
    public CurrenciesServiceImplTest(CurrencyClient currencyClient, CurrencyServiceImpl currencyService) {
        this.currencyClient = currencyClient;
        this.currencyService = currencyService;
    }

    @BeforeEach
    public void setUp() {
        setField(currencyService, "baseCurrencyCode", "BAS");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "RU", "RUBL", "rub", "0RU", "%RU"})
    void givenGetCurrencyDifference_whenGetsInvalidTargetCurrencyCode_thenThrowsIllegalArgumentException(String targetCurrencyCode) {
        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyDifference(targetCurrencyCode));
    }

    @ParameterizedTest
    @EmptySource
    void givenGetCurrencyDifference_whenGetsEmptySource_thenThrowsIllegalArgumentException(String targetCurrencyCode) {
        assertThrows(IllegalArgumentException.class, () -> currencyService.getCurrencyDifference(targetCurrencyCode));
    }

    @ParameterizedTest
    @NullSource
    void givenGetCurrencyDifference_whenGetsNullSource_thenThrowsNullPointerException(String targetCurrencyCode) {
        assertThrows(NullPointerException.class, () -> currencyService.getCurrencyDifference(targetCurrencyCode));
    }

    @Test
    void givenGetCurrencyDifference_whenGetsEqualBaseAndTargetCurrencyCodes_thenDoesNotCallCurrencyClient_ReturnsDownEnumValue() {
        assertEquals(DOWN, currencyService.getCurrencyDifference("BAS"));

        verify(currencyClient, never()).getByDate(any(), any());
        verify(currencyClient, never()).getLatest(any());
    }

    @Test
    void givenGetCurrencyDifference_whenGetsCurrencyDifferenceDown_thenReturnsDownEnumValue() {
        Currency yesterdayRate = createCurrencyWithRates("BAS", 1.7, "TAR", 1.5);
        Currency todayRate = createCurrencyWithRates("BAS", 1.5, "TAR", 1.7);

        when(currencyClient.getLatest(anyString())).thenReturn(todayRate);
        when(currencyClient.getByDate(any(), anyString())).thenReturn(yesterdayRate);

        assertEquals(DOWN, currencyService.getCurrencyDifference("TAR"));
    }

    @Test
    void givenGetCurrencyDifference_whenGetsCurrencyDifferenceUp_thenReturnsUpEnumValue() {
        Currency yesterdayRate = createCurrencyWithRates("BAS", 1.5, "TAR", 1.7);
        Currency todayRate = createCurrencyWithRates("BAS", 1.7, "TAR", 1.5);

        when(currencyClient.getLatest(anyString())).thenReturn(todayRate);
        when(currencyClient.getByDate(any(), anyString())).thenReturn(yesterdayRate);

        assertEquals(UP, currencyService.getCurrencyDifference("TAR"));
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