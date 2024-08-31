package com.cringee.simplescreensharing.interseptors;

import com.cringee.simplescreensharing.annotations.HxRequestOnly;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
@NonNullApi
public class HxRequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();

            if (method.isAnnotationPresent(HxRequestOnly.class) && request.getHeader("HX-Request") == null) {
                response.sendRedirect("/");
                return false;
            }
        }

        return true;
    }
}
