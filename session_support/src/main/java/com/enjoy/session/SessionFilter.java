package com.enjoy.session;

import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Peter on 2018/8/15.
 */
public class SessionFilter implements Filter {
    public static final String USER_INFO = "user";

    private RedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //包装request对象
        MyRequestWrapper myRequestWrapper = new MyRequestWrapper(request,redisTemplate);

        System.out.println("代码1");
        //如果未登陆，则拒绝请求，转向登陆页面
        String requestUrl = request.getServletPath();
        if (!"/toLogin".equals(requestUrl)//不是登陆页面
                && !requestUrl.startsWith("/login")//不是去登陆
                && !myRequestWrapper.isLogin()) {//不是登陆状态

                HttpServletResponse response = (HttpServletResponse)servletResponse;
                response.sendRedirect("http://login.dev.com:8090/toLogin?url="+request.getRequestURL().toString());

                return ;
        }

        try {
            filterChain.doFilter(myRequestWrapper,servletResponse);
        } finally {
            System.out.println("代码2");
            //提交session到redis
            myRequestWrapper.commitSession();
        }


        //dfddddddddddddd
    }

    @Override
    public void destroy() {

    }
}
