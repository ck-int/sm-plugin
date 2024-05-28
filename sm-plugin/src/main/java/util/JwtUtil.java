package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    
    private static final String BASE64SECURITY  ="test";
    private static final Long EXPIRATION = -1L;

    /**
     * 解析token
     * @param jsonWebToken
     * @return
     */
    public static Claims parseToken(String jsonWebToken) {
        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(BASE64SECURITY)).parseClaimsJws(jsonWebToken).getBody();
        return claims;
    }

    /**
     * 新建token
     * @param appId
     * @param appSecret
     * @return
     */
    public static String createToken(String appId, String appSecret) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(BASE64SECURITY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // 添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT").setIssuer(appId).setAudience(appSecret)
                .signWith(signatureAlgorithm, signingKey);

        // 添加Token签发时间
        builder.setIssuedAt(now);
        // 添加Token过期时间
//        if ( -1 >= 0) {
//            long expMillis = nowMillis + EXPIRATION;
//            Date exp = new Date(expMillis);
//            builder.setEXPIRATION(exp).setNotBefore(now);
//        }

        // 生成JWT
        return builder.compact();
    }

    /**
     * 刷新令牌
     *
     * @param claims
     */
    public static void refreshToken(Claims claims) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(BASE64SECURITY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // 添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .setIssuer((String) claims.get("iss")).setAudience((String) claims.get("aud"))
                .signWith(signatureAlgorithm, signingKey);

        // 添加Token签发时间
        builder.setIssuedAt(now);
        // 添加Token过期时间
        if (EXPIRATION >= 0) {
            long expMillis = nowMillis + EXPIRATION;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        // 生成Token
        builder.compact();
    }
}
