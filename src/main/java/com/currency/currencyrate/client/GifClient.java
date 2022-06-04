package com.currency.currencyrate.client;

import com.currency.currencyrate.model.Gif;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;

@FeignClient(name = "${api.gif.client-name}", url = "${api.gif.url}")
public interface GifClient {

    @GetMapping("/random?api_key={appKey}&tag={tagWord}")
    Gif getRandomGifByTag(@PathVariable String appKey, @PathVariable String tagWord);

    @GetMapping(produces = IMAGE_GIF_VALUE)
    byte[] getGifByUrl(URI host);
}