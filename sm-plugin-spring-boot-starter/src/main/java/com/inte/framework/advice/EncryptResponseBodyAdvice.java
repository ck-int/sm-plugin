package com.inte.framework.advice;

import cn.hutool.json.JSONUtil;
import com.inte.framework.annotion.EncryptionAnnotation;
import com.inte.framework.plugin.TestPlugin;
import com.inte.framework.utils.bean.RequestBase;
import com.inte.framework.utils.bean.Result;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;

@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Result<?>> {

    @Resource
    private PluginManager pluginManager;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        try {
            ParameterizedTypeImpl genericParameterType = (ParameterizedTypeImpl) returnType.getGenericParameterType();

            // 如果直接是Result并且有解密注解，则处理
            if (genericParameterType.getRawType() == Result.class && returnType.hasMethodAnnotation(EncryptionAnnotation.class)) {
                return true;
            }

            // 如果不是ResponseBody或者是Result，则放行
            if (genericParameterType.getRawType() != ResponseEntity.class) {
                return false;
            }

            // 如果是ResponseEntity<Result>并且有解密注解，则处理
            for (Type type : genericParameterType.getActualTypeArguments()) {
                if (((ParameterizedTypeImpl) type).getRawType() == Result.class && returnType.hasMethodAnnotation(EncryptionAnnotation.class)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public Result<?> beforeBodyWrite(Result<?> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 真实数据
        Object data = body.getRetData();
        // 如果data为空，直接返回
        if (data == null) {
            return body;
        }
        // 如果是实体，并且继承了Request，则放入时间戳
        if (data instanceof RequestBase) {
            ((RequestBase) data).setCurrentTimeMillis(System.currentTimeMillis());
        }
        String dataText = JSONUtil.toJsonStr(data);
        // 如果data为空，直接返回
        if (StringUtils.isBlank(dataText)) {
            return body;
        }
        //加密
        List<PluginWrapper> plugins = pluginManager.getPlugins();
        if(CollectionUtils.isEmpty(plugins)){
            return body;
        }
        List<TestPlugin> extensions = pluginManager.getExtensions(TestPlugin.class, "sm-plugin");

        if(CollectionUtils.isEmpty(extensions)){
            return body;
        }
        TestPlugin testPlugin = extensions.get(0);
        return testPlugin.encrypt(request,body);
    }


}
