package com.quebecteh.modules.inventary.picklist.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUserAuth;

@Component
public class JwtHelper {
	
	@Value("${spring.application.name}")
	private String appName;
	
	private final String JWT_SECRET_KEY = "098070dcjlkjs&9s@;098798hhh";
	
	public String getEncodedJwt(PickListUserAuth auth) {
		
		ObjectMapper objectMapper = JsonMapper
	            .builder()
	            .addModule(new JavaTimeModule())
	            .build();
		
		String authStr;
		
		try {
			authStr = objectMapper.writeValueAsString(auth);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		} 
		
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET_KEY);

		String token = JWT.create()
			.withClaim("authData", authStr)
			.withIssuer(appName)
			.sign(algorithm);
		
		return token;
	}
	
	
	public String decodeJwt(String jwtToken) {
		try {
		    DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(JWT_SECRET_KEY))
		            .withIssuer(appName)
		            .build()
		            .verify(jwtToken);

		    String auth = decodedJWT.getClaim("authData").asString();
		    return auth;
		    
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new RuntimeException(e.getMessage());
		}
	}

	
}