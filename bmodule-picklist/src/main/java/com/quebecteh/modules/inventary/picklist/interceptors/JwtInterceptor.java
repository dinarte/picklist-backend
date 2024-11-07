package com.quebecteh.modules.inventary.picklist.interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quebecteh.modules.inventary.picklist.controller.JwtHelper;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUserAuth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

	
	@Autowired
	PickListUserAuth auth;
	
	@Autowired
	JwtHelper jwtHelper;
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Extrai o header Authorization
        String authorizationHeader = request.getHeader("Authorization");


        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            
            String tokenJwt = authorizationHeader.substring(7);
            System.out.println("JWT Token: " + tokenJwt); 
            
            
            
            try {
            	String authJson = jwtHelper.decodeJwt(tokenJwt);				
            	ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
            	mapper.readerForUpdating(auth).readValue(authJson);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
            
            
        } else {
            System.out.println("Authorization header is missing or does not contain Bearer token");
        }

        return true;
    }
}