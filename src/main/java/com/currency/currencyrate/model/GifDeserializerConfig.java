package com.currency.currencyrate.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.net.URI;

@JsonComponent
public class GifDeserializerConfig {

    public static class GifDeserializer extends JsonDeserializer<Gif> {
        private GifDeserializer() {
        }

        @Override
        public Gif deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            return new Gif(
                    URI.create(node
                            .get("data")
                            .get("images")
                            .get("original").get("url").asText()));
        }
    }

    private GifDeserializerConfig() {
    }
}