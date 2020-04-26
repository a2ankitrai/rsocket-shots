package com.ank.rsocketgreetingclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonHelper {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    <T> T read(String json, Class<T> clzz) {
        return this.objectMapper.readValue(json, clzz);
    }

    @SneakyThrows
    String write(Object o) {
        return this.objectMapper.writeValueAsString(o);
    }
}
