# rsocket-shots

Reactive Service built on top of RSocket protocol.

Two different implementations are done:

- RSocket native implementation in which we have to setup the connection in both server(r-socket-greeting-service) and client(r-socket-greeting-client). 

- RSocket spring based implementation in which spring configuration does the connection in both server(r-socket-spring-service) and client(r-socket-spring-client)
