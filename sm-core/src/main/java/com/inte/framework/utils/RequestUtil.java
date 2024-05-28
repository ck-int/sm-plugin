package com.inte.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RequestUtil {

    public static String getBody(HttpServletRequest request) {
        try (InputStream is = request.getInputStream()) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.error("read http request failed.", ex);
        }
        return "";
    }
}
