package com.currency.currencyrate.service.impl;

import com.currency.currencyrate.client.CurrencyClient;
import com.currency.currencyrate.model.Currency;
import com.currency.currencyrate.service.CurrencyService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.currency.currencyrate.service.CurrencyService.CurrencyDifference.DOWN;
import static com.currency.currencyrate.service.CurrencyService.CurrencyDifference.UP;
import static java.time.LocalDate.now;
import static java.time.ZoneId.of;
import static java.time.temporal.ChronoUnit.DAYS;


@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    @Value("${api.currency.appId}")
    private String appId;

    @Value("${api.currency.zoneId}")
    private String zoneIdStr;

    @Value("${api.currency.baseCurrencyCode}")
    private String baseCurrencyCode;

    private final CurrencyClient currencyClient;

    @Override
    public CurrencyDifference getCurrencyDifference(@NonNull String targetCurrencyCode) {
        if (!targetCurrencyCode.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("CurrencyCode must be valid");
        }

        if (targetCurrencyCode.equals(baseCurrencyCode)) {
            return DOWN;
        }

        Currency todayRate = currencyClient.getLatest(appId);

        double todayTargetCurrencyValue = todayRate.getRates().get(targetCurrencyCode);
        double todayBaseCurrencyValue = todayRate.getRates().get(baseCurrencyCode);

        Currency yesterdayRate = currencyClient.getByDate(now(of(zoneIdStr)).minus(1, DAYS), appId);

        double yesterdayTargetCurrencyValue = yesterdayRate.getRates().get(targetCurrencyCode);
        double yesterdayBaseCurrencyValue = yesterdayRate.getRates().get(baseCurrencyCode);

        return isCurrencyDifferenceUp(todayTargetCurrencyValue, todayBaseCurrencyValue, yesterdayTargetCurrencyValue, yesterdayBaseCurrencyValue) ? UP : DOWN;
    }

    private static boolean isCurrencyDifferenceUp(
            double todayTargetCurrencyValue, double todayBaseCurrencyValue,
            double yesterdayTargetCurrencyValue, double yesterdayBaseCurrencyValue) {

        return (todayTargetCurrencyValue / todayBaseCurrencyValue) < (yesterdayTargetCurrencyValue / yesterdayBaseCurrencyValue);
    }
}