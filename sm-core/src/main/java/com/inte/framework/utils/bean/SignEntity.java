package com.inte.framework.utils.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignEntity {
    private String appId;
    private String timeStamp;
    private String sign;
    private String nonce;


}

