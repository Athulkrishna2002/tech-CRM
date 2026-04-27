package com.helloWorld.tech.service.impl;

import com.helloWorld.tech.model.converter.UserConverter;
import com.helloWorld.tech.model.dao.UserDao;
import com.helloWorld.tech.model.dto.UserDetailsDto;
import com.helloWorld.tech.model.dto.UserDto;
import com.helloWorld.tech.repository.UserRepository;
import com.helloWorld.tech.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        UserDao toSave = UserConverter.toEntity(userDto);
        toSave.setId(null);
        UserDao saved = userRepository.save(toSave);
        return UserConverter.toDTO(saved);
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        UserDao existing = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

        if (userDto.getName() != null) existing.setName(userDto.getName());
        if (userDto.getEmail() != null) existing.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null) existing.setPassword(userDto.getPassword());
        if (userDto.getStatus() != null) existing.setStatus(userDto.getStatus());
        if (userDto.getUserGrpId() != null) existing.setUserGrpId(userDto.getUserGrpId());

        UserDao saved = userRepository.save(existing);
        return UserConverter.toDTO(saved);
    }

    @Override
    public UserDto getUserById(Integer id) {
        UserDao user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
        return UserConverter.toDTO(user);
    }

    @Override
    public List<UserDetailsDto> searchUsersByName(String name) {
        String normalized = (name == null || name.isBlank()) ? null : name.trim();
        return userRepository.findAllByNameLikeOrAll(normalized);
    }
}

