package com.yuan.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yuan.dto.Result;
import com.yuan.entity.Shop;
import com.yuan.entity.ShopType;
import com.yuan.service.IShopTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static com.yuan.utils.RedisConstants.CACHE_SHOP_LIST;


@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("list")
    public Result queryTypeList() {
        //1.查詢redis查詢商店列表
        String shopListJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_LIST);
        if(StrUtil.isNotBlank(shopListJson)){
            //1-1.不是空值直接返回
            List<Shop> shopList = JSONUtil.toList(shopListJson, Shop.class);
            return Result.ok(shopList);
        }
        //2.沒有則從數據庫查詢並放在redis中
        List<ShopType> typeList = typeService
                .query().orderByAsc("sort").list();
        String shopsJson = JSONUtil.toJsonStr(typeList);
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_LIST,shopsJson);
        return Result.ok(shopsJson);
    }
}