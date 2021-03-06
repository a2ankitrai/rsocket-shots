package com.ank.rsocketspringclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class RSocketSpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RSocketSpringClientApplication.class, args);
    }

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder.connectTcp("localhost", 6900).block();
    }
}

@Component
@RequiredArgsConstructor
@Log4j2
class Consumer {

    private final RSocketRequester rSocketRequester;

    @EventListener(ApplicationReadyEvent.class)
    public void consume() {

        this.rSocketRequester
                .route("greetings.{timeInSeconds}", 2)
                .data(new GreetingRequest("Ankit"))
                .retrieveFlux(GreetingResponse.class)
                .subscribe(log::info);
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {
    private String name;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse {
    private String message;
}
