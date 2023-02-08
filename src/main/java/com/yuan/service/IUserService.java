package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.dto.LoginFormVO;
import com.yuan.dto.Result;
import com.yuan.entity.User;

import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormVO loginForm, HttpSession session);

}
