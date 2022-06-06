package com.currency.currencyrate.service.impl;

import com.currency.currencyrate.client.GifClient;
import com.currency.currencyrate.model.Gif;
import com.currency.currencyrate.service.GifService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {GifServiceImpl.class})
class GifServiceImplTest {

    @MockBean
    private GifClient gifClient;

    private final GifService gifService;

    @Autowired
    public GifServiceImplTest(GifClient gifClient, GifService gifService) {
        this.gifClient = gifClient;
        this.gifService = gifService;
    }

    @Test
    void givenGetRandomGifByTag_whenCallsGetRandomGifAndGetGifByUrl_thenReturnsGifByteArray() {
        byte[] gifByteArray = new byte[12];
        Gif returnedGif = new Gif();
        returnedGif.setOriginalGifUri(java.net.URI.create("www.test.com"));

        when(gifClient.getRandomGifByTag(anyString(), anyString())).thenReturn(returnedGif);
        when(gifClient.getGifByUrl(java.net.URI.create("www.test.com"))).thenReturn(gifByteArray);

        assertEquals(gifByteArray, gifService.getRandomGifByTag("tag"));

        verify(gifClient, times(1)).getRandomGifByTag(anyString(), eq("tag"));
        verify(gifClient, times(1)).getGifByUrl(java.net.URI.create("www.test.com"));
    }

}