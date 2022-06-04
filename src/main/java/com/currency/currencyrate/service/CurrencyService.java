package com.currency.currencyrate.service;

public interface CurrencyService {
    enum currencyDifference {
        UP, DOWN
    }

    currencyDifference getCurrencyDifference(String targetCurrencyCode);
}
