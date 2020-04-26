package com.ank.rsocketgreetingservice;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
public class RSocketGreetingServiceApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(RSocketGreetingServiceApplication.class, args);
        System.in.read();
    }

}

@Component
@Log4j2
@RequiredArgsConstructor
class Producer {

    private final JsonHelper jsonHelper;
    private final GreetingService greetingService;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {

        log.info("Starting Producer...");

        SocketAcceptor socketAcceptor = (connectionSetupPayload, senderRSocket) -> {
            AbstractRSocket response = new AbstractRSocket() {
                @Override
                public Flux<Payload> requestStream(Payload payload) {

                    String json = payload.getDataUtf8();
                    GreetingRequest request = jsonHelper.read(json, GreetingRequest.class);

                    return greetingService.greet(request)
                            .map(jsonHelper::write)
                            .map(DefaultPayload::create);
                }
            };
            return Mono.just(response);
        };

        TcpServerTransport tcpServerTransport = TcpServerTransport.create(6500);

        RSocketFactory
                .receive()
                .acceptor(socketAcceptor)
                .transport(tcpServerTransport)
                .start()
                .block();
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

@Service
class GreetingService {

    Flux<GreetingResponse> greet(GreetingRequest request) {

        return Flux.fromStream(Stream.generate(() ->
                new GreetingResponse("Hello " + request.getName() + " @ " + Instant.now())))
                .delayElements(Duration.ofSeconds(1));

// 		implementation without lambda
//		return Flux.fromStream(Stream.generate(new Supplier<GreetingResponse>() {
//			@Override
//			public GreetingResponse get() {
//				return new GreetingResponse("Hello "+request.getName() + " @ "+ Instant.now());
//			}
//		})).delayElements(Duration.ofSeconds(1));


    }
}