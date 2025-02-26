package edu.rico.security.services;

import java.util.List;
import java.util.Optional;

import edu.rico.security.entities.User;
import edu.rico.security.entities.dto.UserDto;
import edu.rico.security.request.UserRequest;

public interface IUserService {
    List<UserDto> findAll();

    Optional<UserDto> findById(Long id);

    UserDto save(User user);
    Optional<UserDto> update(UserRequest user, Long id);

    void remove(Long id);
}
