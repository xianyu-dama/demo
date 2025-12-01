package com.xianyu.component.web.intercepter;

import jakarta.servlet.Servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

/**
 * get和post要分开
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class LogRequestBodyIntercepter extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    /**
     * 请求体解析前处理
     *
     * @param httpInputMessage
     * @param methodParameter
     * @param type
     * @param aClass
     * @return
     * @throws IOException
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage,
            MethodParameter methodParameter,
            Type type,
            Class<? extends HttpMessageConverter<?>> aClass) throws IOException {

        byte[] content = StreamUtils.copyToByteArray(httpInputMessage.getBody());
        String body = new String(content, StandardCharsets.UTF_8);
        log.info("request-body {}", body);
        return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return httpInputMessage.getHeaders();
            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(content);
            }
        };
    }
}
