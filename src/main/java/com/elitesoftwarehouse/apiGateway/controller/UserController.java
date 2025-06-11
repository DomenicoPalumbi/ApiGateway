package com.elitesoftwarehouse.apiGateway.controller;

import com.elitesoftwarehouse.apiGateway.entity.User;
import com.elitesoftwarehouse.apiGateway.entity.UserDTO;
import com.elitesoftwarehouse.apiGateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    @Transactional(rollbackFor = Exception.class)
    public String saveDto(@RequestBody UserDTO userDto) {
        userDto.setPassword(bCryptPasswordEncoder
                .encode(userDto.getPassword()));
        return userService.save(new User(userDto)).getId().toString();
    }
}