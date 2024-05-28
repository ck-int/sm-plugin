package com.inte.framework.advice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inte.framework.annotion.DecryptionAnnotation;
import com.inte.framework.plugin.TestPlugin;
import lombok.SneakyThrows;
import org.apache.tomcat.websocket.AuthenticationException;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@ControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    private PluginManager pluginManager;

    /**
     * 方法上有DecryptionAnnotation注解的，进入此拦截器
     *
     * @param methodParameter 方法参数对象
     * @param targetType      参数的类型
     * @param converterType   消息转换器
     * @return true，进入，false，跳过
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(DecryptionAnnotation.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    /**
     * 转换之后，执行此方法，解密，赋值
     *
     * @param body          spring解析完的参数
     * @param inputMessage  输入参数
     * @param parameter     参数对象
     * @param targetType    参数类型
     * @param converterType 消息转换类型
     * @return 真实的参数
     */
    @SneakyThrows
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取request
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if (servletRequestAttributes == null) {
            throw new AuthenticationException("request错误");
        }
        //解析token
        List<PluginWrapper> plugins = pluginManager.getPlugins();
        if(CollectionUtils.isEmpty(plugins)){
            return body;
        }
        List<TestPlugin> extensions = pluginManager.getExtensions(TestPlugin.class, "sm-plugin");
        if(CollectionUtils.isEmpty(extensions)){
            return body;
        }
        TestPlugin testPlugin = extensions.get(0);
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String s = testPlugin.decryption(request);
        Object result = objectMapper.readValue(s, body.getClass());
        return result;


    }

    /**
     * 如果body为空，转为空对象
     *
     * @param body          spring解析完的参数
     * @param inputMessage  输入参数
     * @param parameter     参数对象
     * @param targetType    参数类型
     * @param converterType 消息转换类型
     * @return 真实的参数
     */
    @SneakyThrows
    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String typeName = targetType.getTypeName();
        Class<?> bodyClass = Class.forName(typeName);
        return bodyClass.newInstance();
    }
}
