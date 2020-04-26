package com.ank.rsocketgreetingclient;

import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class RSocketGreetingClientApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(RSocketGreetingClientApplication.class, args);
        System.in.read();
    }

}

@Component
@RequiredArgsConstructor
@Log4j2
class Consumer {

    private final JsonHelper jsonHelper;

    @EventListener(ApplicationReadyEvent.class)
    public void consume() {

        log.info("Starting Consumer...");

        String jsonRequest = jsonHelper.write(new GreetingRequest("Ankit"));

        TcpClientTransport tcpClientTransport = TcpClientTransport.create(6500);

        RSocketFactory
                .connect()
                .transport(tcpClientTransport)
                .start()
                .flatMapMany(sender -> sender.requestStream(DefaultPayload.create(jsonRequest)))
                .map(Payload::getDataUtf8)
                .map(json -> jsonHelper.read(json, GreetingResponse.class))
                .subscribe(result -> log.info(result.toString()));

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
