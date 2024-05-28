package com.inte.framework.plugin;

import com.inte.framework.utils.bean.Result;
import org.apache.tomcat.websocket.AuthenticationException;
import org.pf4j.ExtensionPoint;
import org.springframework.http.server.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author ck
 */
public interface TestPlugin extends ExtensionPoint {

    /**
     * 校验token
     * @param request 请求
     * @return 是否存在
     */
    Boolean checkToken(HttpServletRequest request) throws IOException;

    /**
     * 解密
     * @param request 请求
     * @return
     */
    String decryption(HttpServletRequest request) throws IOException, AuthenticationException;

    /**
     * 加密
     *
     * @param request 请求
     * @param body
     * @return
     */
    Result encrypt(ServerHttpRequest request,Result body);

    /**
     * 登录校验
     * @param request 请求
     */
    Boolean loginValidator(HttpServletRequest request);

}
