package com.currency.currencyrate.service.impl;

import com.currency.currencyrate.client.GifClient;
import com.currency.currencyrate.model.Gif;
import com.currency.currencyrate.service.GifService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GifServiceImpl implements GifService {

    @Value("${api.gif.appId}")
    private String appId;

    private final GifClient gifClient;

    @Override
    public byte[] getRandomGifByTag(String tag) {
        Gif gif = gifClient.getRandomGifByTag(appId, tag);

        return gifClient.getGifByUrl(gif.getOriginalGifUri());
    }
}