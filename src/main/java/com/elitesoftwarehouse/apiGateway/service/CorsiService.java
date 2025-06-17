package com.elitesoftwarehouse.apiGateway.service;

import com.elitesoftwarehouse.apiGateway.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CorsiService {
    private final WebClient webClient;
    
    public CorsiService(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public Mono<String> registerUser(User user) {
        return webClient.post()
                .uri("/users/nuovo")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(String.class);
    }
} 