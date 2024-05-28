package com.inte.framework.utils.cache;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenCache {
    private static final LoadingCache<String, String> localcache =
            CacheBuilder.newBuilder()
                    .initialCapacity(1000)
                    .maximumSize(10000)
                    .expireAfterAccess(24, TimeUnit.HOURS)
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String s) {
//                        为什么要把return的null值写成字符串，因为到时候用null去.equal的时候，会报空指针异常
                            return "null";
                        }
                    });

    /*
     * 添加本地缓存
     * */
    public static void setKey(String key, String value) {
        localcache.put(key, value);
    }
    /*
     * 得到本地缓存
     * */
    public static String getKey(String key) {
        String value = null;
        try {
            value= localcache.get(key);
            if ("null".equals(value)) {
                return  null;
            }
            return value;
        } catch (ExecutionException e) {
            log.error("getKey()方法错误",e);
        }
        return null;
    }


}
