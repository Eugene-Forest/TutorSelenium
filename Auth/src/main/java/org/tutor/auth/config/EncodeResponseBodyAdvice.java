package org.tutor.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.tutor.auth.anno.AesRequest;
import org.tutor.auth.units.AnnoUnits;

/**
 * @author Eugene-Forest
 * {@code @date} 2024/12/9
 */
@RestControllerAdvice(annotations = AesRequest.class)
//@RestControllerAdvice(basePackages = {"org.tutor"})
public class EncodeResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(EncodeResponseBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AnnoUnits.ifExistAnno(returnType.getMethod(), AesRequest.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        log.debug("EncodeResponseBodyAdvice beforeBodyWrite");
        return body;
    }
}