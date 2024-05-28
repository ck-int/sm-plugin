package com.inte.framework.advice;

import com.alibaba.fastjson.JSON;
import com.inte.framework.annotion.LoginValidator;
import com.inte.framework.plugin.TestPlugin;
import com.inte.framework.utils.bean.AppServiceEntity;
import com.inte.framework.utils.bean.SignEntity;
import com.inte.framework.utils.bean.TokenBean;
import com.inte.framework.utils.sign.SignUtil;
import com.inte.framework.utils.sm4.Sm4Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * 登录拦截
 */
@Slf4j
@Component
@Aspect
public class LoginAspect {


    @Resource
    private PluginManager pluginManager;

    /**
     * 切点，方法上有注解或者类上有注解
     * 拦截标有注解的类或者方法
     */
    @Pointcut(value = "@annotation(com.inte.framework.annotion.LoginValidator)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object before(ProceedingJoinPoint joinpoint) throws Throwable {
        // 获取方法方法上的LoginValidator注解
        MethodSignature methodSignature = (MethodSignature) joinpoint.getSignature();
        Method method = methodSignature.getMethod();
        LoginValidator loginValidator = method.getAnnotation(LoginValidator.class);
        // 如果有，并且值为false，则不校验
        if (loginValidator != null && !loginValidator.validated()) {
            return joinpoint.proceed(joinpoint.getArgs());
        }
        // 正常校验 获取request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null || requestAttributes.getResponse() == null) {
            // 如果不是从前端过来的，没有request，则直接放行
            return joinpoint.proceed(joinpoint.getArgs());
        }
        HttpServletRequest request = requestAttributes.getRequest();
        List<PluginWrapper> plugins = pluginManager.getPlugins();
        if(CollectionUtils.isEmpty(plugins)){
            return joinpoint.proceed(joinpoint.getArgs());
        }
        List<TestPlugin> extensions = pluginManager.getExtensions(TestPlugin.class, "sm-plugin");
        TestPlugin testPlugin = extensions.get(0);
        Boolean b = testPlugin.loginValidator(request);
        if(!b){
            throw new AuthenticationException("认证失败");
        }
        // 放行
        return joinpoint.proceed(joinpoint.getArgs());
    }


}
