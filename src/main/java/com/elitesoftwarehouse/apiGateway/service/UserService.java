package com.elitesoftwarehouse.apiGateway.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.elitesoftwarehouse.apiGateway.entity.User;
import com.elitesoftwarehouse.apiGateway.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User update(User user) {
        if (userRepository.existsById(user.getId())) {
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + user.getId());
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}