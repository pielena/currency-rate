package com.currency.currencyrate.controller;

import com.currency.currencyrate.service.CurrencyService;
import com.currency.currencyrate.service.GifService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

import static com.currency.currencyrate.service.CurrencyService.CurrencyDifference.UP;
import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class CurrencyController {
    private static final Pattern CURRENCY_PATTERN = compile("^[A-Z]{3}$");

    private final Logger logger = getLogger(CurrencyController.class);

    private final CurrencyService currencyService;

    private final GifService gifService;

    @Value("${api.currency.baseCurrencyCode}")
    private String baseCurrencyCode;

    @Value("${api.gif.currency-difference-up}")
    private String currencyDifferenceUp;

    @Value("${api.gif.currency-difference-down}")
    private String currencyDifferenceDown;

    @GetMapping(value = "/difference", produces = MediaType.IMAGE_GIF_VALUE)
    public byte[] getGifByCurrencyDifference(@RequestParam(name = "code") String targetCurrencyCode) {
        validateCurrencyCode(targetCurrencyCode);

        CurrencyService.CurrencyDifference currencyDifference = currencyService.getCurrencyDifference(targetCurrencyCode);

        logger.info("Target currency: {}; base currency: {}; currency difference: {}", targetCurrencyCode, baseCurrencyCode, currencyDifference);

        String tagWord = currencyDifference == UP ? currencyDifferenceUp : currencyDifferenceDown;

        return gifService.getRandomGifByTag(tagWord);
    }

    private void validateCurrencyCode(String currencyCode) {
        if (!CURRENCY_PATTERN.matcher(currencyCode).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid currency code");
        }
    }
}