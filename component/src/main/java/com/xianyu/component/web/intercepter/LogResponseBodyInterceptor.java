package com.xianyu.component.web.intercepter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static com.xianyu.component.utils.json.JsonUtils.toJSONString;

/**
 * 打印reponse<br/>
 * Created on : 2023-10-13 18:12
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Component
public class LogResponseBodyInterceptor extends OncePerRequestFilter {

    public static final int MAX_RESPONSE_BODY_LENGTH = 1024 * 1024 * 4;

    private static void log(byte[] responseBody, Map<String, String> headers) {

        if (log.isDebugEnabled() || responseBody.length == 0) {
            return;
        }

        if (Objects.nonNull(responseBody) && responseBody.length > MAX_RESPONSE_BODY_LENGTH) {
            log.warn("response-body length greater than {} ignore log", MAX_RESPONSE_BODY_LENGTH);
            return;
        }

        log.debug("response-header {} response-body {}", toJSONString(headers), new String(responseBody, StandardCharsets.UTF_8));
    }

    private static Map<String, String> getHeaders(HttpServletResponse response) {
        return response.getHeaderNames()
            .stream()
            .collect(Collectors.toMap(h -> h, response::getHeader, (v1, v2) -> v1 + v2));
    }

    private static byte[] getResponseBody(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException {
        byte[] responseBody = new byte[0];
        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(response);
        try {
            // Execution request chain
            filterChain.doFilter(request, resp);
            responseBody = resp.getContentAsByteArray();
        } catch (Exception e) {
            log.error("请求异常", e);
        } finally {
            // Finally remember to respond to the client with the cached data.
            resp.copyBodyToResponse();
        }
        return responseBody;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (("text/event-stream".equalsIgnoreCase(response.getContentType())
            || "text/event-stream".equalsIgnoreCase(request.getHeader("Accept")))
            && request.getRequestURI().contains("/sse")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (log.isDebugEnabled()) {
            byte[] responseBody = getResponseBody(request, response, filterChain);
            log(responseBody, getHeaders(response));
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
