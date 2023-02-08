package com.yuan.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Page;
import cn.hutool.json.JSONUtil;
import com.yuan.dto.Result;
import com.yuan.entity.Shop;
import com.yuan.mapper.ShopMapper;
import com.yuan.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.utils.CacheClient;
import com.yuan.utils.RedisData;
import com.yuan.utils.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.yuan.utils.RedisConstants.*;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    @Override
    public Result queryById(Long id) {
        // 解決緩存穿透
        Shop shop = cacheClient
                .queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        // 互斥鎖解決緩存擊穿
        // Shop shop = cacheClient
        //         .queryWithMutex(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        // 邏輯過期解決緩存擊穿
        // Shop shop = cacheClient
        //         .queryWithLogicalExpire(CACHE_SHOP_KEY, id, Shop.class, this::getById, 20L, TimeUnit.SECONDS);

        if (shop == null) {
            return Result.fail("店鋪不存在！");
        }
        // 7.返回
        return Result.ok(shop);
    }

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店鋪id不能為空");
        }
        // 1.更新數據庫
        updateById(shop);
        // 2.刪除緩存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return Result.ok();
    }

//    @Override
//    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
//        // 1.判斷是否需要根據坐標查詢
//        if (x == null || y == null) {
//            // 不需要坐標查詢，按數據庫查詢
//            Page<Shop> page = query()
//                    .eq("type_id", typeId)
//                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
//            // 返回數據
//            return Result.ok(page.getClass());
//        }
//
//        // 2.計算分頁參數
//        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
//        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;
//
//        // 3.查詢redis、按照距離排序、分頁。結果：shopId、distance
//        String key = SHOP_GEO_KEY + typeId;
//        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo() // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
//                .search(
//                        key,
//                        GeoReference.fromCoordinate(x, y),
//                        new Distance(5000),
//                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
//                );
//        // 4.解析出id
//        if (results == null) {
//            return Result.ok(Collections.emptyList());
//        }
//        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
//        if (list.size() <= from) {
//            // 沒有下一頁了，結束
//            return Result.ok(Collections.emptyList());
//        }
//        // 4.1.截取 from ~ end的部分
//        List<Long> ids = new ArrayList<>(list.size());
//        Map<String, Distance> distanceMap = new HashMap<>(list.size());
//        list.stream().skip(from).forEach(result -> {
//            // 4.2.獲取店鋪id
//            String shopIdStr = result.getContent().getName();
//            ids.add(Long.valueOf(shopIdStr));
//            // 4.3.獲取距離
//            Distance distance = result.getDistance();
//            distanceMap.put(shopIdStr, distance);
//        });
//        // 5.根據id查詢Shop
//        String idStr = StrUtil.join(",", ids);
//        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
//        for (Shop shop : shops) {
//            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
//        }
//        // 6.返回
//        return Result.ok(shops);
//    }
}
