package com.elitesoftwarehouse.apiGateway.service;

import com.elitesoftwarehouse.apiGateway.model.dto.UserDTO;
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
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());

        return webClient.post()
                .uri("/users/nuovo")
                .bodyValue(dto)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Errore API: " + errorBody))))
                .bodyToMono(String.class);
    }
}