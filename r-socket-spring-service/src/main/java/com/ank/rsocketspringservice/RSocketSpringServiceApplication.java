package com.ank.rsocketspringservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
public class RSocketSpringServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RSocketSpringServiceApplication.class, args);
	}

}

@Controller
@RequiredArgsConstructor
class GreetingsController{

	private final GreetingService greetingService;

	@MessageMapping("greetings.{timeInSeconds}")
	Flux<GreetingResponse> greet(@DestinationVariable("timeInSeconds") int timeInSeconds,
								 GreetingRequest greetingRequest){
		return this.greetingService.greet(greetingRequest,timeInSeconds);
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

	Flux<GreetingResponse> greet(GreetingRequest request, int timeInSeconds) {

		return Flux.fromStream(Stream.generate(() ->
				new GreetingResponse("Hello " + request.getName() + " @ " + Instant.now())))
				.delayElements(Duration.ofSeconds(timeInSeconds));


	}

}
