package com.yuan;

import cn.hutool.json.JSONUtil;
import com.yuan.entity.ShopType;
import com.yuan.service.IShopTypeService;
import com.yuan.service.impl.ShopServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private IShopTypeService typeService;

    @Autowired
    private ShopServiceImpl shopService;


    @Test
    void TestJSON(){
        List<ShopType> typeList = typeService
                .query().orderByAsc("sort").list();
        String shopsJson = JSONUtil.toJsonStr(typeList);
        System.out.println(shopsJson);
    }
    @Test
    void TestSaveShop(){
        shopService.saveShop2Redis(1L, (long) 10L);
    }

    @Test
    void TestLambda(){
        @Test
        public void test1(){

            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    System.out.println("我爱北京天安门");
                }
            };

            r1.run();

            System.out.println("***********************");

            Runnable r2 = () -> System.out.println("我爱北京故宫");

            r2.run();
            Runnable r3 = System.out::println;
        }
    }
}
