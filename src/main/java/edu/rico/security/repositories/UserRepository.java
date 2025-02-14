package edu.rico.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.rico.security.entities.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

}
