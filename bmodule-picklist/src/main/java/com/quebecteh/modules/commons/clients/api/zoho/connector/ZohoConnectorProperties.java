package com.quebecteh.modules.commons.clients.api.zoho.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Component
@RequiredArgsConstructor
public class ZohoConnectorProperties {

    private final HttpServletRequest request;
	
	@Value("${spring.application.name}")
	private String appName;
	
	@Value("${bmodule-picklist.zoho.oauth.baseUrl}")
    private String authBaseUrl;

    @Value("${bmodule-picklist.zoho.oauth.clientId}")
    private String clientId;

    @Value("${bmodule-picklist.zoho.oauth.clientSecret}")
    private String clientSecret;

    @Value("${bmodule-picklist.zoho.oauth.scop}")
    private String scope;

    @Value("${bmodule-picklist.zoho.client.api.baseUrl}")
    private String apiBaseUrl;
    
    @Value("${bmodule-picklist.https}")
    private String https;

    public String getAuthUrl(String tenantId, String authCallback)  {
        String url = authBaseUrl + "/auth?client_id=" + clientId + "&response_type=code&scope=" + scope + "&redirect_uri=" 
                                    + authCallback + "&state=" + tenantId + "&access_type=offline&prompt=consent";
        
        System.out.println("AUTH URL:");
        System.out.println(url);
        
        return url;

    }

    public String getTokenUrl(String code, String authCallback, String tenantId) {
        String url = authBaseUrl + "/token?code="+code+"&client_id="+clientId+"&client_secret="+clientSecret+"&redirect_uri="
                                    +authCallback+"&grant_type=authorization_code"+ "&state=" + tenantId;
        return url;
    }
    
    public String getRefreshTokenURL(String refreshToken, String authCallback) {
    	String url = authBaseUrl + "/token?refresh_token="+refreshToken+"&client_id="+clientId+"&client_secret="+clientSecret
    						+"&redirect_uri="+authCallback+"&grant_type=refresh_token";
    	return url;
    }
    
    public String getRevokeRefreshTokenURL(String refreshToken) {
    	String url = authBaseUrl + "/token/revoke?token="+refreshToken;
    	return url;
    }
    
    public String getApiUrl() {
        return apiBaseUrl;
    }
    
    public String getConnectionUrl(String tenentId) {
    	String baseUrl = getBaseAppContextUrl();
        String authPath = "/zoho/"+tenentId+"/auth";
        String authUrl = baseUrl + authPath;
        return authUrl; 
    }
    
    public String getCallbackUrl() {
        String baseUrl = getBaseAppContextUrl();
        String authCallback = "/zoho/auth/callback";
        String callbackUrl = baseUrl + authCallback;
        return callbackUrl;
    }
    
    public String getBaseAppContextUrl() {
    	String protocol = Boolean.valueOf(https) ? "https" : "http";
    	String port = request.getServerPort() != 80 ? ":" + request.getServerPort() : "";
    	return protocol + "://" + request.getServerName() + port + request.getContextPath();
    }
    
    
    
}
