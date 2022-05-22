package com.itheima.reggie.dto;

import com.itheima.reggie.domain.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String code;
}
