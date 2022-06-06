package com.currency.currencyrate.service;

public interface CurrencyService {
    enum CurrencyDifference {
        UP, DOWN
    }

    CurrencyDifference getCurrencyDifference(String targetCurrencyCode);
}