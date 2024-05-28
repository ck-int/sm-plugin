package com.inte.framework.filter;
import cn.hutool.json.JSONUtil;
import com.inte.framework.config.LoginProperties;
import com.inte.framework.plugin.TestPlugin;
import com.inte.framework.utils.result.R;
import lombok.RequiredArgsConstructor;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


@Component
@RequiredArgsConstructor
public class LoginFilter implements Filter {

    private final LoginProperties loginProperties;

    private final PluginManager pluginManager;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        // 过滤路径
        String requestURI = httpServletRequest.getRequestURI();
        if (!loginProperties.getFilterExcludeUrl().contains(requestURI)) {
            //TODO 算法插件
            List<PluginWrapper> plugins = pluginManager.getPlugins();
            if(CollectionUtils.isEmpty(plugins)){
                chain.doFilter(request, response);
                return;
            }

            List<TestPlugin> extensions = pluginManager.getExtensions(TestPlugin.class, "sm-plugin");
            TestPlugin testPlugin = extensions.get(0);
            Boolean hasToken = testPlugin.checkToken(httpServletRequest);
            if(!hasToken){
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                returnNoLogin(httpServletResponse,"token认证失败！");
            }
        }
        chain.doFilter(request, response);
    }
    private void returnNoLogin(HttpServletResponse response,String msg) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        // 设置返回401 和响应编码
        response.setStatus(401);
        response.setContentType("Application/json;charset=utf-8");
        // 构造返回响应体
        R error = R.error(500, msg);

        String resultString = JSONUtil.toJsonStr(error);
        outputStream.write(resultString.getBytes(StandardCharsets.UTF_8));
    }

}
