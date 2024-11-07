package com.quebecteh.modules;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.quebecteh.modules.inventary.picklist.interceptors.JwtInterceptor;
import com.quebecteh.modules.inventary.picklist.interceptors.TenantIdVerifierInterceptior;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

   
    private final JwtInterceptor jwtInterceptor;
    
    private final TenantIdVerifierInterceptior tenantIdVerifierInterception;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(tenantIdVerifierInterception).addPathPatterns("/**");
        registry.addInterceptor (jwtInterceptor).addPathPatterns("/**");
    }
}