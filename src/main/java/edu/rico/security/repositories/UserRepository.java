package edu.rico.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.rico.security.entities.User;

public interface UserRepository
        extends JpaRepository<User, Long> {
        
        Optional<User> findByUsername(String username);

}
