package com.yuan.controller;


import com.yuan.dto.LoginFormVO;
import com.yuan.dto.Result;
import com.yuan.dto.UserVO;
import com.yuan.entity.UserInfo;
import com.yuan.service.IUserInfoService;
import com.yuan.service.IUserService;
import com.yuan.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@Slf4j@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * 發送手機驗證碼
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // TODO 發送短信驗證碼並保存驗證碼

        return userService.sendCode(phone,session);
    }

    /**
     * 登錄功能
     * @param loginForm 登錄參數，包含手機號、驗證碼；或者手機號、密碼
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormVO loginForm, HttpSession session){
        // TODO 實現登錄功能
        return userService.login(loginForm, session);
    }

    /**
     * 登出功能
     * @return 無
     */
    @PostMapping("/logout")
    public Result logout(){
        // TODO 實現登出功能
        return Result.fail("功能未完成");
    }

    @GetMapping("/me")
    public Result me(){
        // TODO 獲取當前登錄的用戶並返回
        UserVO user = UserHolder.getUser();
        return Result.ok(user);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId){
        // 查詢詳情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 沒有詳情，應該是第一次查看詳情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }
}