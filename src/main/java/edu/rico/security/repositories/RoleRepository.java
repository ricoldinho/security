package edu.rico.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.rico.security.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

}
