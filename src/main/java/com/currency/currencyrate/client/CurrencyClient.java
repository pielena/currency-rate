package com.currency.currencyrate.client;

import com.currency.currencyrate.model.Currency;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

@FeignClient(value = "${api.currency.client-name}", url = "${api.currency.url}")
public interface CurrencyClient {

    @GetMapping("/latest.json?app_id={appId}")
    Currency getLatest(@PathVariable String appId);

    @GetMapping("/historical/{date}.json?app_id={appId}")
    Currency getByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PathVariable String appId);
}