package com.yuan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.dto.Result;
import com.yuan.entity.Shop;
import com.yuan.service.IShopService;
import com.yuan.utils.SystemConstants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    public IShopService shopService;

    /**
     * 根據id查詢商舖信息
     * @param id 商舖id
     * @return 商舖詳情數據
     */
    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") Long id) {
        return shopService.queryById(id);
    }

    /**
     * 新增商舖信息
     * @param shop 商舖數據
     * @return 商舖id
     */
    @PostMapping
    public Result saveShop(@RequestBody Shop shop) {
        // 寫入數據庫
        shopService.save(shop);
        // 返回店鋪id
        return Result.ok(shop.getId());
    }

    /**
     * 更新商舖信息
     * @param shop 商舖數據
     * @return 無
     */
    @PutMapping
    public Result updateShop(@RequestBody Shop shop) {
        // 寫入數據庫

        return shopService.update(shop);
    }

    /**
     * 根據商舖類型分頁查詢商舖信息
     * @param typeId 商舖類型
     * @param current 頁碼
     * @return 商舖列表
     */
    @GetMapping("/of/type")
    public Result queryShopByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根據類型分頁查詢
        Page<Shop> page = shopService.query()
                .eq("type_id", typeId)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        // 返回數據
        return Result.ok(page.getRecords());
    }

    /**
     * 根據商舖名稱關鍵字分頁查詢商舖信息
     * @param name 商舖名稱關鍵字
     * @param current 頁碼
     * @return 商舖列表
     */
    @GetMapping("/of/name")
    public Result queryShopByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根據類型分頁查詢
        Page<Shop> page = shopService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 返回數據
        return Result.ok(page.getRecords());
    }
}