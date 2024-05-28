import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inte.framework.utils.RequestUtil;
import com.inte.framework.utils.bean.*;
import com.inte.framework.utils.cache.TokenCache;
import com.inte.framework.utils.sign.SignUtil;
import com.inte.framework.utils.sm4.Sm4Util;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.ObjectUtils;
import util.JwtUtil;
import com.inte.framework.plugin.TestPlugin;
import org.pf4j.Extension;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Extension(ordinal = 1)
@Slf4j
public class AuthPlugin implements  TestPlugin {

    /**
     * 校验token
     * @param request 请求
     * @return 是否成功
     */
    @Override
    public Boolean checkToken(HttpServletRequest request) {
        // 获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            return false;
        }
        //认证token
        try {
            Claims claims = JwtUtil.parseToken(token);
            //刷新
            JwtUtil.refreshToken(claims);
        }catch (Exception e){
            log.error("token解析失败");
            return false;
        }
        String value = TokenCache.getKey(token);
        AppServiceEntity appServiceEntity = JSONObject.parseObject(value, AppServiceEntity.class);
        if(ObjectUtils.isEmpty(appServiceEntity)){
            return false;
        }
        return true;
    }

    /**
     * 解密
     * @param request 请求
     * @return 请求体
     */
    @Override
    public String decryption(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        Claims claims = JwtUtil.parseToken(token);
        String iv = claims.get("iss").toString();
        String secretKey = claims.get("aud").toString();
        String json = RequestUtil.getBody(request);
        JSONObject jsonObject = JSONObject.parseObject(json);
        return Sm4Util.sm4Decrypt(secretKey, iv, jsonObject.getString("data"));
    }

    /**
     * 加密算法
     * @param request 请求
     * @param body 返回体
     * @return 返回体
     */
    @Override
    public Result encrypt(ServerHttpRequest request, Result body) {
        String dataText = JSONUtil.toJsonStr(body.getRetData());
        List<String> list = request.getHeaders().get("authorization");
        String token = list.get(0);
        Claims claims = JwtUtil.parseToken(token);
        String iv = claims.get("iss").toString();
        String secretKey = claims.get("aud").toString();
        //加密
        String encryptText = Sm4Util.sm4Encrypt(secretKey, iv, dataText);
        //获取时间戳
        Long time = DateUtil.date().getTime();
        String value = TokenCache.getKey(token);
        String nonce = SignUtil.generateNonce();
        AppServiceEntity appServiceEntity = JSONObject.parseObject(value, AppServiceEntity.class);
        String responseSign = SignUtil.createResponseSign(appServiceEntity.getAppId(), time.toString(), nonce, encryptText, appServiceEntity.getAppSecret());
        return Result.builder()
                .retCode(body.getRetCode())
                .retData(encryptText)
                .retMsg(body.getRetMsg())
                .timeStamp(time)
                .sign(responseSign)
                .build();
    }

    @Override
    public Boolean loginValidator(HttpServletRequest request) {
        // 获取签名
        SignEntity signEntity = SignUtil.getSignEntity(request);
        // 验证签名
        AppServiceEntity appServiceEntity = SignUtil.LoginSign(signEntity.getAppId(), getBody(request), signEntity);
        if(ObjectUtils.isEmpty(appServiceEntity)){
            return false;
        }
        //生成iv 和 secretKey
        TokenBean tokenBean = Sm4Util.createTokenBean();
        //根据iv 和 secretKey 生成token
        String token = JwtUtil.createToken(tokenBean.getIv(), tokenBean.getSecretKey());
        tokenBean.setToken(token);
        request.setAttribute("publicKey", appServiceEntity.getPublicKey());
        request.setAttribute("token", JSON.toJSONString(tokenBean));
        request.setAttribute("appServiceEntity", JSON.toJSONString(appServiceEntity));
        return true;
    }
    public static String getBody (HttpServletRequest request){
        try (InputStream is = request.getInputStream()) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.error("read http request failed.", ex);
        }
        return "";
    }


}
