package com.elitesoftwarehouse.apiGateway.controller;

import com.elitesoftwarehouse.apiGateway.model.entity.User;
import com.elitesoftwarehouse.apiGateway.model.dto.UserDTO;
import com.elitesoftwarehouse.apiGateway.service.UserService;
import com.elitesoftwarehouse.apiGateway.service.CorsiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    private final UserService userService;
    private final CorsiService corsiService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, CorsiService corsiService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.corsiService = corsiService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return Mono.just(ResponseEntity.badRequest().body("Username già esistente!"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.save(user);

        return corsiService.registerUser(user)
                .map(response -> ResponseEntity.ok("Utente registrato con successo in entrambi i sistemi!"))
                .onErrorResume(e -> {
                    userService.delete(savedUser);
                    return Mono.just(ResponseEntity.badRequest().body("Errore durante la registrazione nel servizio corsi: " + e.getMessage()));
                });
    }

    @PostMapping("/users")
    public ResponseEntity<String> saveDto(@RequestBody UserDTO userDto) {
        if (userService.existsByUsername(userDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username già esistente!");
        }

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = new User(userDto);
        User savedUser = userService.save(user);

        return ResponseEntity.ok(savedUser.getId().toString());
    }
}
