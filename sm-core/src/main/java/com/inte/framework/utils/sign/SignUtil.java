package com.inte.framework.utils.sign;


import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inte.framework.utils.bean.AppServiceEntity;
import com.inte.framework.utils.bean.SignEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Optional;

public class SignUtil {
    /**
     * 登录加密签名
     * @param appId qppid
     * @param body 请求体
     * @param signEntity 签名
     * @return AppServiceEntity
     */
    public static AppServiceEntity LoginSign(String appId, String body, SignEntity signEntity) {
        AppServiceEntity appServiceEntity = getAppServiceEntityByAppId(appId);
        //验证签名
        String nonce = signEntity.getNonce();
        String timeStamp = signEntity.getTimeStamp();
        String sign = appId + timeStamp + nonce;
        JSONObject jsonObject = JSON.parseObject(body);
        String data = jsonObject.getString("data");
        if (StringUtils.isBlank(data)) {
            throw new AuthenticationException("签名错误！");
        }
        sign += data;
        sign = SmUtil.sm3WithSalt(appServiceEntity.getAppSecret().getBytes(StandardCharsets.UTF_8)).digestHex(sign).toUpperCase();
        if (!sign.equals(signEntity.getSign())) {
            throw new AuthenticationException("签名错误！");
        }
        //解密Body sm2
        SM2 sm2 = SmUtil.sm2(appServiceEntity.getPrivateKey(), appServiceEntity.getPublicKey());
        String s = sm2.decryptStr(data, KeyType.PrivateKey);
        JSONObject jsonBody = JSONObject.parseObject(s);
        if (!jsonBody.getString("appId").equals(appServiceEntity.getAppId())) {
            throw new AuthenticationException("APPId 错误！");
        }
        if (!jsonBody.getString("appSecret").equals(appServiceEntity.getAppSecret())) {
            throw new AuthenticationException("appSecret 错误！");
        }
        return appServiceEntity;
    }

    public static AppServiceEntity getAppServiceEntityByAppId(String appId) {
       return AppServiceEntity.builder()
                .appId("f70318aid8u639f0svn37c76c6cd02hd1")
                .appName("testApp")
                .appSecret("Ah6uP5VFHKfrvfdRY63VetEqoN287RT73lfkWyC7")
                .privateKey("5b223775e98338707876df5bd40b13c2cf0488a9047e528d43987f29e2a53036")
                .publicKey("04f7c76eb67b247edd7f5456053755d626648003f510a67bf38ffb07265a372caf38633a6b07f703dc5b4eb8af1b0af6f95e8f67b3e17bf2e3f79df11c3a49d0a2")
                .build();
    }

    /**
     * 从请求里获取签名
     * @param request 请求
     * @return 签名
     */
    public static SignEntity getSignEntity(HttpServletRequest request) {
        // 获取签名
        String appId = request.getHeader("appId");
        String timeStamp = request.getHeader("timeStamp");
        String sign = request.getHeader("sign");
        String nonce = request.getHeader("nonce");
        appId = Optional.ofNullable(appId).orElseThrow(() -> new AuthenticationException("appId 不存在！"));
        timeStamp = Optional.ofNullable(timeStamp).orElseThrow(() -> new AuthenticationException("timeStamp 不存在！"));
        sign = Optional.ofNullable(sign).orElseThrow(() -> new AuthenticationException("sign 不存在！"));
        nonce = Optional.ofNullable(nonce).orElseThrow(() -> new AuthenticationException("nonce 不存在！"));
        return SignEntity.builder()
                .sign(sign)
                .appId(appId)
                .timeStamp(timeStamp)
                .nonce(nonce)
                .build();

    }
    public static String createResponseSign(String appId,String timestamp,String nonce,String data, String appSecret){
        String signStr = appId + timestamp + nonce + data;
        return SmUtil.sm3WithSalt(appSecret.getBytes(StandardCharsets.UTF_8)).digestHex(signStr).toUpperCase();
    }

    public static String generateNonce() {
        SecureRandom random = new SecureRandom();
        byte[] nonceBytes = new byte[16];
        random.nextBytes(nonceBytes);
        return new BigInteger(1, nonceBytes).toString(36); // Convert to base 36 for URL safety
    }

}
