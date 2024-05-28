package com.inte.framework.utils.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppServiceEntity {
    private String appName;

    private String appId;

    private String appSecret;

    private String publicKey;

    private String privateKey;

    private String sign;
}
