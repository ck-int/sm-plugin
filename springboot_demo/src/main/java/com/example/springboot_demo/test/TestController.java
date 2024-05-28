package com.example.springboot_demo.test;
import com.alibaba.fastjson.JSONObject;
import com.example.springboot_demo.test.entity.ReleaseTaskEntity;
import com.inte.framework.annotion.DecryptionAnnotation;
import com.inte.framework.annotion.EncryptionAnnotation;
import com.inte.framework.annotion.LoginValidator;
import com.inte.framework.utils.bean.Result;
import com.inte.framework.utils.bean.TokenBean;
import com.inte.framework.utils.cache.TokenCache;
import com.inte.framework.utils.result.R;
import com.inte.framework.utils.sm2.Sm2Util;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("test")
public class TestController {

    //认证
    @LoginValidator
    @PostMapping("authentication")
    public R authentication(HttpServletRequest httpServletRequest) {
        String token = (String) httpServletRequest.getAttribute("token");
        String appServiceEntityJson = (String) httpServletRequest.getAttribute("appServiceEntity");
        String publicKey = (String) httpServletRequest.getAttribute(Sm2Util.KEY_PUBLIC_KEY);
        String s = Sm2Util.encryptBase64(token, publicKey);
        Map<String, Object> data = new HashMap<>();
        data.put("data", s);
        TokenBean tokenBean = JSONObject.parseObject(token, TokenBean.class);
        //放入缓存
        TokenCache.setKey(tokenBean.getToken(), appServiceEntityJson);
        return R.ok(data);
    }


    @DecryptionAnnotation
    @PostMapping("test")
    public Result test( @RequestBody ReleaseTaskEntity releaseTaskEntity){
        System.out.println(releaseTaskEntity.toString());
        return Result.builder().retCode(200).retData(releaseTaskEntity.toString()).build();
    }


    @EncryptionAnnotation
    @GetMapping("encryptionTest")
    public Result<Object> encryptionTest(@Validated @RequestBody ReleaseTaskEntity releaseTaskEntit){
        System.out.println(releaseTaskEntit.toString());
        return Result.builder().retCode(200).retData(releaseTaskEntit.toString()).build();
    }




}
