package com.yuan.service.impl;

import com.yuan.entity.Blog;
import com.yuan.mapper.BlogMapper;
import com.yuan.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

}
