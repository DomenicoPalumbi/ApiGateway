package com.elitesoftwarehouse.apiGateway.repository;

import com.elitesoftwarehouse.apiGateway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {  // Cambiato da String a Long
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}