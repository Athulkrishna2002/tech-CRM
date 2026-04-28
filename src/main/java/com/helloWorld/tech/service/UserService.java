package com.helloWorld.tech.service;

import com.helloWorld.tech.model.dto.UserDetailsDto;
import com.helloWorld.tech.model.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Integer id, UserDto userDto);

    UserDto getUserById(Integer id);

    List<UserDetailsDto> searchUsersByName(String name);
}
