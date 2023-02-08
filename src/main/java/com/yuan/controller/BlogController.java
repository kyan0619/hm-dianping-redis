package com.yuan.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.dto.Result;
import com.yuan.dto.UserVO;
import com.yuan.entity.Blog;
import com.yuan.entity.User;
import com.yuan.service.IBlogService;
import com.yuan.service.IUserService;
import com.yuan.utils.SystemConstants;
import com.yuan.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private IBlogService blogService;
    @Autowired
    private IUserService userService;

    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        // 獲取登入用戶
        UserVO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 保存探店blog
        blogService.save(blog);
        // 返回id
        return Result.ok(blog.getId());
    }

    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        // 修改點贊數量
        blogService.update()
                .setSql("liked = liked + 1").eq("id", id).update();
        return Result.ok();
    }

    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 獲取登錄用戶
        UserVO user = UserHolder.getUser();
        // 根據用戶查詢
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 獲取當前頁數據
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 根據用戶查詢
        Page<Blog> page = blogService.query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 獲取當前頁數據
        List<Blog> records = page.getRecords();
        // 查詢用戶
        records.forEach(blog ->{
            Long userId = blog.getUserId();
            User user = userService.getById(userId);
            blog.setName(user.getNickName());
            blog.setIcon(user.getIcon());
        });
        return Result.ok(records);
    }
}