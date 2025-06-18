package com.elitesoftwarehouse.apiGateway.controller;

import com.elitesoftwarehouse.apiGateway.model.AuthRequest;
import com.elitesoftwarehouse.apiGateway.model.AuthResponse;
import com.elitesoftwarehouse.apiGateway.model.entity.User;
import com.elitesoftwarehouse.apiGateway.model.dto.UserDTO;
import com.elitesoftwarehouse.apiGateway.service.CustomUserDetailsService;
import com.elitesoftwarehouse.apiGateway.service.UserService;
import com.elitesoftwarehouse.apiGateway.service.CorsiService;
import com.elitesoftwarehouse.apiGateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    private final UserService userService;
    private final CorsiService corsiService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthController(UserService userService, CorsiService corsiService, PasswordEncoder passwordEncoder, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.corsiService = corsiService;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody User user) {
        return Mono.fromCallable(() -> userService.existsByUsername(user.getUsername()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.just(ResponseEntity.badRequest().body("Username già esistente!"));
                    }

                    user.setPassword(passwordEncoder.encode(user.getPassword()));

                    return Mono.fromCallable(() -> userService.save(user))  // Salva davvero
                            .flatMap(savedUser -> corsiService.registerUser(user)
                                    .map(response -> ResponseEntity.ok("Utente registrato con successo in entrambi i sistemi!"))
                                    .onErrorResume(e -> {
                                        userService.delete(savedUser);
                                        return Mono.just(ResponseEntity.badRequest()
                                                .body("Errore durante la registrazione nel servizio corsi: " + e.getMessage()));
                                    }));
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest authRequest) {
        return customUserDetailsService.findByUsername(authRequest.getUsername())
                .flatMap(userDetails -> {
                    boolean passwordMatch = passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword());
                    if (!passwordMatch) {
                        return Mono.just(ResponseEntity.status(401).body("Password errata"));
                    }

                    String token = jwtUtil.generateToken(
                            userDetails.getUsername(),
                            userDetails.getAuthorities().stream().findFirst().get().getAuthority()
                    );

                    return Mono.just(ResponseEntity.ok(new AuthResponse(token)));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(404).body("Utente non trovato")));
    }
}