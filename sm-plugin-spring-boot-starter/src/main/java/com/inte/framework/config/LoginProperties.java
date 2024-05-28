package com.inte.framework.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;


@Component
@ConfigurationProperties(prefix = "login")
@Data
public class LoginProperties implements Serializable {
    private static final long serialVersionUID = 7052116364833723328L;

    private List<String> filterIncludeUrl;
    private List<String> filterExcludeUrl;

    private List<String> interceptorIncludeUrl;
    private List<String> interceptorExcludeUrl;

}
