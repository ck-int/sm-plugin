import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inte.framework.utils.sm4.Sm4Util;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;

@Slf4j
public class TestUnit {
    public static final String APP_ID = "f70318aid8u639f0svn37c76c6cd02hd1";
    public static final String APP_SECRET = "Ah6uP5VFHKfrvfdRY63VetEqoN287RT73lfkWyC7";
    public static final String PUBLIC_KEY = "04f7c76eb67b247edd7f5456053755d626648003f510a67bf38ffb07265a372caf38633a6b07f703dc5b4eb8af1b0af6f95e8f67b3e17bf2e3f79df11c3a49d0a2";
    public static final String PRIVATE_KEY = "5b223775e98338707876df5bd40b13c2cf0488a9047e528d43987f29e2a53036";


    public static void main(String[] args) {
//        testToken();
//        testDecrypt();
        testEncrypt();

    }

    /**
     * 登录加密流程
     */
    public static void testToken() {
        //获取时间戳
        long time = DateUtil.date().getTime();
        log.info("timeStamp:{}", time);
        //获取随机数
        String nonce = generateNonce();
        log.info("nonce:{}", nonce);

        String sign = APP_ID + time + nonce;
        JSONObject data = new JSONObject();
        data.put("appId", APP_ID);
        data.put("appSecret", APP_SECRET);
        SM2 sm2 = SmUtil.sm2(PRIVATE_KEY, PUBLIC_KEY);
        String s = sm2.encryptBase64(data.toJSONString(), KeyType.PublicKey);
        log.info("data:{}",s);
        String s1 = SmUtil.sm3WithSalt(APP_SECRET.getBytes(StandardCharsets.UTF_8)).digestHex(sign);
        log.info("sign:{}",s1.toUpperCase());
    }


    public static void testDecrypt(){
        SM2 sm2 = SmUtil.sm2(PRIVATE_KEY, PUBLIC_KEY);
        String retDate = "BG7XyJK5vwNbVuKALfizu27sv3yZok2MIx8bSheraEMAFzeUpmamg+Vgp04UXZdHAzKAX4bXdViuNIgGgyEE09Ps8zgr9qJkngHGvTXW8+03qx1rn26816uMaVt3u+0eH8sRC2623yxBY8AfE/oHZT63Jc6PMVRUjdanu+UrfTUIN4eY92kQB7nxogC4l4QZwlU9DXFyJsuaGFa6y1l34HPzjWl+0GDwsHMukoCqp8BXU+OXYqIY3QLmiUOXCYCfmq7E90VV4tiSlgVKemzmIp6SxAhogOQjV2Ftml0jKFV+bZt97214iZwi3w5YYzSDwihcoX4ikb1BeMtRKNsvMdHu6M8UJnWuGIFeHTBZVnvpMssXfDpFeBBTm/KWOiIrJb4Txim0pHvO12D4d77z+Vt6tuabjBEV8BTQ/WppD/TyiV/q9jjA4nq/kn0HfTql";
        String res = sm2.decryptStr(retDate, KeyType.PrivateKey);
        System.out.println(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        String iv = jsonObject.getString("iv");
        String secretKey = jsonObject.getString("secretKey");
        String token = jsonObject.getString("token");
        ReleaseTaskEntity build = ReleaseTaskEntity.builder()
                .beginTime(111L)
                .endTime(111L)
                .taskCode("001")
                .cnStationNo("1111")
                .gpStationNo("2222").build();
        String sm4Data = Sm4Util.sm4Encrypt(secretKey, iv, JSON.toJSONString(build));
        //获取时间戳
        long time = DateUtil.date().getTime();
        log.info("time:{}", time);

        //获取随机数
        String nonce = generateNonce();
        log.info("nonce:{}", nonce);
        log.info("data:{}", sm4Data);

        String sign = APP_ID+ time + nonce;
        sign += sm4Data;
        String s1 = SmUtil.sm3WithSalt(APP_SECRET.getBytes(StandardCharsets.UTF_8)).digestHex(sign);
        log.info("sign {}", s1.toUpperCase());
    }






    public static String generateNonce() {
        SecureRandom random = new SecureRandom();
        byte[] nonceBytes = new byte[16];
        random.nextBytes(nonceBytes);
        return new BigInteger(1, nonceBytes).toString(36); // Convert to base 36 for URL safety
    }




    public static void testEncrypt(){
        String text ="qZqp1TA01ODilMiIYkm56l7LeAh3gSRrJw0BlnQusNiYNv/7aL5+bQnYu2YJaIc/Pv55ColYxGykD2a2f3M1ACCuxDRJvGekLvuOrtjUaYGzeEPQxXXuwFKRYRmYYC0I+A+/yCSBEFL3vaYXvGkh0KvcydRC++FXHe7ak5yyC/M=";
        //解密
        String iv = "2muvhgmxtg3a9qt7";
        String secretKey = "6y9hp6s75bby4brj";
        String s = Sm4Util.sm4Decrypt(secretKey, iv, text);

        System.out.println(s);


    }


}

