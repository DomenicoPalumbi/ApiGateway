package com.elitesoftwarehouse.apiGateway.config;

import com.elitesoftwarehouse.apiGateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return Mono.justOrEmpty(authHeader)
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .flatMap(token -> {
                    String username = jwtUtil.extractUsername(token);
                    if (username == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token non valido"));
                    }

                    return reactiveUserDetailsService.findByUsername(username)
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non trovato")))
                            .flatMap(userDetails -> {
                                if (jwtUtil.validateToken(token, userDetails)) {
                                    Authentication auth = new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                                    return chain.filter(exchange)
                                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                                } else {
                                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token scaduto o invalido"));
                                }
                            });
                })
                .switchIfEmpty(chain.filter(exchange)); // se non c'è Authorization header
    }
}
