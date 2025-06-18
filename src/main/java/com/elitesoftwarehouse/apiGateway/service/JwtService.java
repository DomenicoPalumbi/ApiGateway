package com.elitesoftwarehouse.apiGateway.service;

import com.elitesoftwarehouse.apiGateway.model.AuthRequest;
import com.elitesoftwarehouse.apiGateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class JwtService {

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<String> createJwtToken(AuthRequest authRequest) {
        return userDetailsService.findByUsername(authRequest.getUsername())
                .flatMap(userDetails -> {
                    if (passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
                        String role = userDetails.getAuthorities().iterator().next().getAuthority(); // es: "ROLE_ADMIN"
                        String token = jwtUtil.generateToken(userDetails.getUsername(), role);
                        return Mono.just(token);
                    } else {
                        return Mono.error(new RuntimeException("Credenziali non valide"));
                    }
                });
    }
}
