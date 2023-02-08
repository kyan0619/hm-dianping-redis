package com.yuan.dto;

import lombok.Data;

@Data
public class LoginFormVO {
    private String phone;
    private String code;
    private String password;
}
