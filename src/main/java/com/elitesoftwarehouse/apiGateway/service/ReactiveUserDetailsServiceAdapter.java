package com.elitesoftwarehouse.apiGateway.service;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReactiveUserDetailsServiceAdapter implements ReactiveUserDetailsService {

    private final CustomUserDetailsService blockingUserDetailsService;

    public ReactiveUserDetailsServiceAdapter(CustomUserDetailsService blockingUserDetailsService) {
        this.blockingUserDetailsService = blockingUserDetailsService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        try {
            UserDetails userDetails = blockingUserDetailsService.loadUserByUsername(username);
            return Mono.just(userDetails);
        } catch (UsernameNotFoundException e) {
            return Mono.error(e);
        }
    }
}
