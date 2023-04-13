package com.jay.gulistore.member.interceptor;


import com.jay.common.constant.AuthServerConstant;
import com.jay.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {
//    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();
    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();
    /**
     * 用户登录拦截器
     * @param request
     * @param response
     * @param handler
     * @return
     *      用户登录：放行
     *      用户未登录：跳转到登录页面
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 这个请求直接放行
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if(match){
            return true;
        }
        // 获取session
        HttpSession session = request.getSession();
        // 获取登录用户
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(memberRespVo != null){
            loginUser.set(memberRespVo);
            return true;
        }else{
            // 没登陆就去登录
            session.setAttribute("msg", AuthServerConstant.NOT_LOGIN);
            response.sendRedirect("http://auth.sukestore.com/login.html");
            return false;
        }
    }
}
