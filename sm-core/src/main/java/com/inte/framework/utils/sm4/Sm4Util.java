package com.inte.framework.utils.sm4;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import com.inte.framework.utils.bean.TokenBean;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Sm4Util {


    /**
     * SM4 对称加密算法
     *
     * @param data 待加密数据
     * @param key  密钥(密钥长度必须为128位，需进行转换)
     * @return 加密结果
     */
    public static String sm4Encrypt(String data, String key) {
        SM4 sm4 = SmUtil.sm4(toSm4Key(key));
        byte[] encrypt = sm4.encrypt(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encode(encrypt);
    }

    /**
     *1.ECB模式 优点：实现简单，适合处理小数据 缺点：相同的明文分组会产生相同的密文分组，容易被模式识别攻击
     *2.CBC模式 优点：相同的明文块会因不同的IV生成不同的密文块，安全性较高 缺点：加密需顺序处理，不支持并行加密；解密时的错误会传播
     *3.CFB模式 优点：支持加密数据流，可以加密小于一个分组的数据；加密和解密使用相同算法 缺点：错误会传播
     *4.OFB模式 优点：类似CFB模式，不会传播解密错误；支持并行处理 缺点：不适合加密长时间数据流，因IV重复会泄露信息
     *5.CTR模式 优点：支持并行加密，效率高；错误不会传播
     *6.PCBC模式 优点：更安全 缺点更复杂，可能需要更多的计算资源，效率低
     *
     * SM4 对称加密算法
     * @param data 待加密数据
     * @return 加密结果
     */
    public static String sm4Encrypt(String sm4Key, String sm4Iv, String data) {
        SM4 sm4 = new SM4(Mode.CBC, Padding.PKCS5Padding, sm4Key.getBytes(StandardCharsets.UTF_8), sm4Iv.getBytes(StandardCharsets.UTF_8));
        return sm4.encryptBase64(data);
    }

    public static String sm4Decrypt(String sm4Key, String sm4Iv, String data) {
        SM4 sm4 = new SM4(Mode.CBC, Padding.PKCS5Padding, toSm4Key(sm4Key), sm4Iv.getBytes(StandardCharsets.UTF_8));
        return sm4.decryptStr(data);
    }


    /**
     * SM4 对称解密算法
     *
     * @param data 待解密数据
     * @param key  密钥(密钥长度必须为128位，需进行转换)
     * @return 解密结果
     */
    public static String sm4Decrypt(String data, String key) {
        SM4 sm4 = SmUtil.sm4(toSm4Key(key));
        byte[] decrypt = sm4.decrypt(Base64.decode(data));
        return new String(decrypt, StandardCharsets.UTF_8);
    }


    /**
     * 将密钥转换为128位长度
     *
     * @param key 密钥
     * @return 128位长度的密钥
     */
    public static byte[] toSm4Key(String key) {
        return Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);
    }

    public static TokenBean createTokenBean() {
        String secretKey = RandomUtil.randomString(16);
        String iv = RandomUtil.randomString(16);
        return TokenBean.builder()
                .secretKey(secretKey)
                .iv(iv)
                .build();
    }
}
