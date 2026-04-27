package com.helloWorld.tech.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Integer id;
    private String name;
    private String email;
    private String password;
    private Integer status;
    private Integer userGrpId;
}