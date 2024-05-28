package com.inte.framework.utils.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenBean {
    private String token;

    private String secretKey;

    private String iv;

}
