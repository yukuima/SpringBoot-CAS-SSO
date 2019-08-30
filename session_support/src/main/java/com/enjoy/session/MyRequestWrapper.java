package com.enjoy.session;

import com.enjoy.utils.CookieBasedSession;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Peter on 2018/8/15.
 * request包装类
 */
public class MyRequestWrapper extends HttpServletRequestWrapper {
    private volatile boolean committed = false;
    private String uuid = UUID.randomUUID().toString();

    private MySession session;
    private RedisTemplate redisTemplate;

    public MyRequestWrapper(HttpServletRequest request,RedisTemplate redisTemplate) {
        super(request);
        this.redisTemplate = redisTemplate;
    }

    /**
     * 提交session内值到redis
     */
    public void commitSession() {
        if (committed) {
            return;
        }
        committed = true;

        MySession session = this.getSession();
        if (session != null && null != session.getAttrs()) {
            redisTemplate.opsForHash().putAll(session.getId(),session.getAttrs());
        }
    }

    /**
     * 创建新session
     * @return
     */
    public MySession createSession() {

        String sessionId = CookieBasedSession.getRequestedSessionId(this);
        Map<String,Object> attr ;
        if (null != sessionId){
            attr = redisTemplate.opsForHash().entries(sessionId);
        } else {
            System.out.println("create session by rId:"+uuid);
            sessionId = UUID.randomUUID().toString();
            attr = new HashMap<>();
        }

        //session成员变量持有
        session = new MySession();
        session.setId(sessionId);
        session.setAttrs(attr);

        return session;
    }

    /**
     * 取session
     * @return
     */
    public MySession getSession() {
        return this.getSession(true);
    }

    /**
     * 取session
     * @return
     */
    public MySession getSession(boolean create) {
        if (null != session){
            return session;
        }
        return this.createSession();
    }

    /**
     * 是否已登陆
     * @return
     */
    public boolean isLogin(){
        Object user = getSession().getAttribute(SessionFilter.USER_INFO);
        return null != user;
    }

}
