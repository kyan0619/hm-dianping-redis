package com.yuan.Interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yuan.dto.UserVO;
import com.yuan.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.yuan.utils.RedisConstants.LOGIN_USER_KEY;
import static com.yuan.utils.RedisConstants.LOGIN_USER_TTL;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1. get token
        String token = request.getHeader("authorization");
        // check token is exits
        if(StrUtil.isBlank(token)){
            return true;
        }
        //2.get User from token
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY+token);
            //3.check user if exits
            if(userMap.isEmpty()){
                //if not exits ，return
                return true;
        }
        //3. userMap change to UserVO
        UserVO userVO = BeanUtil.fillBeanWithMap(userMap, new UserVO(), false);
        //4. exits save user into ThreadLocal
        UserHolder.saveUser((UserVO) userVO);
        //5. refresh token Validity period
        stringRedisTemplate.expire(LOGIN_USER_KEY+token,LOGIN_USER_TTL, TimeUnit.DAYS);
        return true;
    }
}
