package edu.rico.security.services;

import java.util.List;
import java.util.Optional;

import edu.rico.security.entities.User;
import edu.rico.security.request.UserRequest;

public interface IUserService {
    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);
    Optional<User> update(UserRequest user, Long id);

    void remove(Long id);
}
