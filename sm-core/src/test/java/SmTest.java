import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.digest.SM3;
import com.inte.framework.utils.bean.TokenBean;
import com.inte.framework.utils.sm2.Sm2Util;
import com.inte.framework.utils.sm4.Sm4Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class SmTest {
    public static final String APP_ID = "f70318aid8u639f0svn37c76c6cd02hd1";
    public static final String APP_SECRET = "Ah6uP5VFHKfrvfdRY63VetEqoN287RT73lfkWyC7";
    public static final String PUBLIC_KEY = "04f7c76eb67b247edd7f5456053755d626648003f510a67bf38ffb07265a372caf38633a6b07f703dc5b4eb8af1b0af6f95e8f67b3e17bf2e3f79df11c3a49d0a2";
    public static final String PRIVATE_KEY = "5b223775e98338707876df5bd40b13c2cf0488a9047e528d43987f29e2a53036";

    /**
     * sm2非对称加密算法 其他的算法如 RSA，ECDSA，DH
     * sm2 算法测试
     */
    @Test
    public void sm2Test() {
        //获取privateKey 和publicKey
        Map<String, String> keyMap = Sm2Util.generateSm2Key();
        log.info("【sm2获取公钥和私钥】={}", keyMap);
        String text = "aaaaa";
        //公钥加密
        String encryptedText = Sm2Util.encryptBase64(text, keyMap.get(Sm2Util.KEY_PUBLIC_KEY));
        log.info("【sm2加密】={}", encryptedText);
        //私钥解密
        String decryptedText = Sm2Util.decryptBase64(encryptedText, keyMap.get(Sm2Util.KEY_PRIVATE_KEY));
        log.info("【sm2解密】={}", decryptedText);
        //##########sm2签名############
        log.info("#################sm2签名######################");
        //私钥签名
        String sign = Sm2Util.sign(keyMap.get(Sm2Util.KEY_PRIVATE_KEY), text);
        log.info("【sm2签名】={}", sign);
        //公钥验签
        boolean verify = Sm2Util.verify(keyMap.get(Sm2Util.KEY_PUBLIC_KEY), text, sign);
        log.info("【sm2验签】={}", verify);
    }

    /**
     * 它主要用于生成固定长度的消息摘要（hash value），以确保数据的完整性和防篡改性 类似于md5
     * sm3 哈希函数算法 测试
     */
    @Test
    public void Sm3Test() {
        String text = "bbbbbbbbb";
        String sign = SmUtil.sm3WithSalt("aaa".getBytes(StandardCharsets.UTF_8)).digestHex(text);
        log.info("【sm3加密】={}", sign);
    }

    /**
     *
     * sm4对称加密测试
     */
    @Test
    public void Sm4Test() {
        String text = "ccccc";
        TokenBean tokenBean = Sm4Util.createTokenBean();
        //加密
        String encryptText = Sm4Util.sm4Encrypt(tokenBean.getSecretKey(), tokenBean.getIv(), text);
        log.info("【sm4加密】={}", encryptText);
        //解密
        String decryptText = Sm4Util.sm4Decrypt(tokenBean.getSecretKey(), tokenBean.getIv(), encryptText);
        log.info("【sm4解密】={}", decryptText);

    }


}






