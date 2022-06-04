package com.currency.currencyrate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CurrencyRateApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyRateApplication.class, args);
    }
}
