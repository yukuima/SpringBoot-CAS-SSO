package com.enjoy.session;

import com.enjoy.utils.CookieBasedSession;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

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
    private String sessionId = null;

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

        String psessionId = (sessionId != null)?sessionId:CookieBasedSession.getRequestedSessionId(this);
        Map<String,Object> attr ;
        if (!StringUtils.isEmpty(psessionId)){
            attr = redisTemplate.opsForHash().entries(psessionId);
        } else {
            System.out.println("create session by rId:"+sessionId);
            psessionId = UUID.randomUUID().toString();
            attr = new HashMap<>();
        }

        //session成员变量持有
        session = new MySession();
        session.setId(psessionId);
        session.setAttrs(attr);

        return session;
    }

    /**
     * 取session
     * @return
     */
    @Override
    public MySession getSession() {
        return this.getSession(true);
    }

    /**
     * 取session
     * @return
     */
    @Override
    public MySession getSession(boolean create) {
        if (null != session){
            return session;
        }
        return this.createSession();
    }

    /**
     * 判断是否已登陆
     * @return
     */
    public boolean isLogin(){
        Object user = getSession().getAttribute(CasFilter.USER_INFO);
        return null != user;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
