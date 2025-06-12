package com.elitesoftwarehouse.apiGateway.controller;

import com.elitesoftwarehouse.apiGateway.model.entity.User;
import com.elitesoftwarehouse.apiGateway.model.dto.UserDTO;
import com.elitesoftwarehouse.apiGateway.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username già esistente!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return ResponseEntity.ok("Utente registrato con successo!");
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
