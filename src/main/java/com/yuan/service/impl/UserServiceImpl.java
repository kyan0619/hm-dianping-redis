package com.yuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.dto.LoginFormVO;
import com.yuan.dto.Result;
import com.yuan.dto.UserVO;
import com.yuan.entity.User;
import com.yuan.mapper.UserMapper;
import com.yuan.service.IUserService;
import com.yuan.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.yuan.utils.RedisConstants.*;
import static com.yuan.utils.SystemConstants.USER_NICK_NAME_PREFIX;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //1.驗證手機格式是否正確
       if(RegexUtils.isPhoneInvalid(phone)){
           log.debug("手機號碼不正確");
           //2.如果不符合，返回錯誤訊息
           return Result.fail("手機號碼格是錯誤");
       }
       //3.如果正確，產生驗證碼
        String code = RandomUtil.randomNumbers(6);

       //4.保存驗證碼在redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY+phone,code,LOGIN_CODE_TTL,TimeUnit.MINUTES);
        //5. 寄出驗證碼
        log.debug("驗證碼是 :{}",code);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormVO loginForm, HttpSession session) {
        // 1.校驗手機號
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回錯誤信息
            return Result.fail("手機號格式錯誤！");
        }
        // 3.從redis獲取驗證碼並校驗
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            // 不一致，報錯
            return Result.fail("驗證碼錯誤");
        }

        // 4.一致，根據手機號查詢用戶 select * from tb_user where phone = ?
        User user = query().eq("phone", phone).one();

        // 5.判斷用戶是否存在
        if (user == null) {
            // 6.不存在，創建新用戶並保存
            user = createUserWithPhone(phone);
        }

        // 7.保存用戶信息到 redis中
        // 7.1.隨機生成token，作為登錄憑證
        String token = UUID.randomUUID().toString(true);
        // 7.2.將User對象轉為HashMap存儲
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        Map<String, String> userMap =  new HashMap<>();
        String id = userVO.getId()+"";
        String icon = userVO.getIcon();
        String nickName = userVO.getNickName();
        userMap.put("id",id);
        userMap.put("icon",icon);
        userMap.put("nickname",nickName);
        // 7.3.存儲
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 7.4.設置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.DAYS);

        // 8.返回token
        return Result.ok(token);
    }
    private User createUserWithPhone(String phone) {
        // 1.創建用戶
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 2.保存用戶
        save(user);
        return user;
    }
}
