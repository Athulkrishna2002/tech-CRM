package com.helloWorld.tech.model.converter;

import com.helloWorld.tech.model.dao.UserDao;
import com.helloWorld.tech.model.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class UserConverter {
   public static UserDto toDTO(UserDao entity) {
        if (entity == null) return null;

        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());
        dto.setUserGrpId(entity.getUserGrpId());

        return dto;
    }

    public static UserDao toEntity(UserDto dto) {
        if (dto == null) return null;

        UserDao entity = new UserDao();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setStatus(dto.getStatus());
        entity.setUserGrpId(dto.getUserGrpId());

        return entity;
    }
}