package com.jay.gulistore.cart.interceptor;

import com.jay.common.constant.AuthServerConstant;
import com.jay.common.constant.CartConstant;
import com.jay.common.vo.MemberRespVo;
import com.jay.gulistore.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;


/**
 * 在执行目标方法之前，判断用户登录状态，并封装传递给controller目标请求
 */
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<UserInfoTo>();
    /**
     * 业务执行前拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        //从Session中获取数据
        MemberRespVo member = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        UserInfoTo userInfoTo = new UserInfoTo();
        //1 用户已经登录，设置userId
        if (member != null) {
            userInfoTo.setUserId(member.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                //2 如果cookie中已经有user-Key，则直接设置
                if (CartConstant.TEMP_USER_COOKIE_NAME.equals(name)) {
                    userInfoTo.setUserKey(cookie.getValue());
//                    userInfoTo.setHasUserKey(true);
                    userInfoTo.setTempUser(true);
                    break;
                }
            }
        }
        //3 执行到这里 如果cookie为空即没有user-key（没有临时用户），则我们通过uuid生成user-key分配一个
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        //4 将用户身份认证信息即封装好的UserInfo放入threadlocal进行传递
        threadLocal.set(userInfoTo);
        return true;
    }
    /**
     * 业务执行之后的拦截器，目的是让让浏览器保存Cookie
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //如果cookie中没有临时用户信息user-key，我们为其生成
        if (!userInfoTo.isTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            //设置作用域
            cookie.setPath("sukestore.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}

