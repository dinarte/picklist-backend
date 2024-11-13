package com.quebecteh.modules.commons.connector.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.quebecteh.commons.rest.ApiResponse;
import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorService;
import com.quebecteh.modules.commons.connector.model.domain.ZohoConnection;
import com.quebecteh.modules.commons.connector.model.domain.ZohoOrganization;
import com.quebecteh.modules.commons.connector.model.dto.OrganizationDTO;
import com.quebecteh.modules.commons.connector.service.ZohoConnectionService;
import com.quebecteh.modules.commons.connector.service.ZohoOrganizationService;
import com.quebecteh.modules.inventary.picklist.controller.JwtHelper;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUser;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUserAuth;
import com.quebecteh.modules.inventary.picklist.service.PickListUserService;
import com.quebecteh.modules.inventary.picklist.validators.HttpBadRequestException;
import com.quebecteh.modules.inventary.picklist.validators.HttpResouceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * Controller for handling Zoho authentication and integration-related requests.
 *
 * This controller provides endpoints for managing Zoho authentication and for
 * handling callback requests. It communicates with Zoho API to manage
 * connections, retrieve organizations, and manage authentication tokens.
 */
@Log4j2
@RestController
@RequestMapping("/zoho")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ZohoConnectorController {

    @Autowired
    private ZohoConnectorProperties zohoConnectorProperties;

    @Autowired
    private ZohoConnectorService zohoConnectorService;

    @Autowired
    private ZohoConnectionService connectionService;
    
    @Autowired
    PickListUserService userService;

    @Autowired
    private ZohoOrganizationService organizationService;
    
    @Autowired
    JwtHelper jwtHelper;
    
    @Value("${bmodule-picklist.zoho.postAuth.frontend}")
    private String frontEndUrl; 
    

    /**
     * Handles the authentication request for a given tenant ID.
     * If a connection already exists, redirects directly to the callback endpoint.
     * Otherwise, constructs the authentication URL for Zoho and redirects the user.
     *
     * @param tenantId The tenant ID for which authentication is being requested.
     * @return RedirectView Redirect to the Zoho authentication page or callback URL.
     */
    @GetMapping("/{tenantId}/auth")
    public ApiResponse<String> auth(@PathVariable("tenantId") String tenantId) {
        String callbackUrl = zohoConnectorProperties.getCallbackUrl();
        String url = zohoConnectorProperties.getAuthUrl(tenantId, callbackUrl);
        return new ApiResponse<String>(200, "zoho-auth-url", "Zoho Auth Url", url);
    }

    /**
     * Handles the authentication callback from Zoho after the user has authorized the application.
     * It retrieves or creates a new connection, refreshes the token if expired, and retrieves a list
     * of organizations associated with the authenticated Zoho account.
     *
     * @param tenantId The tenant ID provided by Zoho during authentication.
     * @param code The authorization code provided by Zoho after user consent.
     * @return ResponseEntity A list of OrganizationDTO representing the organizations linked to the tenant.
     */
   
    @SneakyThrows
    @GetMapping("/auth/callback")
    public RedirectView authCallback(
            @RequestParam("state") String tenantId,
            @RequestParam("code") String code) {
   
		var authValuesMap = zohoConnectorService.sendPostAuthentication(tenantId, code);
		var userAuth = zohoConnectorService.getUserInfo(authValuesMap.get("access_token").toString());
		var conn = retriveConnection(tenantId, userAuth.getId(), zohoConnectorProperties.getAppName()); 
		
		if (conn == null) {
		    conn = createNewConnection(tenantId, authValuesMap, userAuth);
		    var user = PickListUser
				    .builder()
			        	.email(userAuth.getUserEmail())
			        	.name(userAuth.getUserName())
			        	.roles("PickListUser")
			        	.password("none")
			        	.tenantId(tenantId)
			        .build();
		    userService.saveOrUpdate(user);
		}   
		
		connectionService.renewConnectionIfExipered(conn, authValuesMap);

			
		userAuth.setConn(conn);
		userAuth.setTenantId(tenantId);
		
		var organizationDtoList = zohoConnectorService.getOrganizations(conn.getAccesToken());
		saveAllOrganizations(tenantId, organizationDtoList, conn);
	    
	 
	    var frontEndConnectionsView = frontEndUrl + "?tenantId="+tenantId+"&auth="	+ jwtHelper.getEncodedJwt(userAuth);
		return new RedirectView(frontEndConnectionsView);
        
    }
    
    @SneakyThrows
    @GetMapping("/auth/revoke/{connectionId}")
    public ApiResponse<Map<String, Object>> revoke(@PathVariable("connectionId") Long connectionId) {
   
    	var conn = connectionService.findById(connectionId)
    					.orElseThrow(() -> 
    						new HttpResouceNotFoundException("Connection ID #"+connectionId+" not found.","connectionId-not-found")
    					);
    	
    	var refreshToken = Optional.of(conn.getRefreshToken()).orElseThrow();
    	
		var zohoResponse = zohoConnectorService.sendPostRevokeRefreshToken(refreshToken);
		var response = new ApiResponse<Map<String, Object>>(200, 
								"connetion-revoked", 
								"Connection ID #"+connectionId+" revoked", 
								zohoResponse
							);
		
		if (response.getBody().get("error") != null)
			throw new HttpBadRequestException(zohoResponse, "zoho-revoke-invalid");
	
		conn.setRefreshToken(null);
		connectionService.saveOrUpdate(conn);
		return response;
        
    }

    /**
     * Saves all retrieved organizations associated with a tenant to the database.
     *
     * @param tenantId The tenant ID for which organizations are being saved.
     * @param organizationDtoList A list of OrganizationDTO objects representing organizations.
     * @param conn The ZohoConnection object associated with the tenant.
     */
    private void saveAllOrganizations(String tenantId, List<OrganizationDTO> organizationDtoList, final ZohoConnection conn) {
        List<ZohoOrganization> organizationsList = organizationDtoList.stream().map(dto -> {
            dto.setInUse(true);
            return ZohoOrganization
                    .builder()
                    .connection(conn)
                    .contactName(dto.getContactName())
                    .country(dto.getCountry())
                    .email(dto.getEmail())
                    .languageCode(dto.getLanguageCode())
                    .name(dto.getName())
                    .organizationId(dto.getOrganizationId())
                    .phone(dto.getPhone())
                    .tenantId(tenantId)
                    .timeZone(dto.getTimeZone())
                    .build();
        }).collect(Collectors.toList());
        organizationService.saveOrUpdateAllByOrganizationId(organizationsList);
    }


    /**
     * Retrieves the Zoho connection associated with a specific tenant.
     *
     * @param tenantId The tenant ID for which to retrieve the connection.
     * @return ZohoConnection The connection associated with the tenant, or null if none exists.
     */
    private ZohoConnection retriveConnection(String tenantId, String userId, String appName) {
    	return connectionService.findFristWhere("tenantId = :tenantId and userId= :userId and appName = :appName", 
    			Map.of(
    					"tenantId",tenantId,  
    					"userId", userId,
    					"appName", appName
    				  )
    			);
    }

    /**
     * Creates a new connection for a tenant based on authentication values received from Zoho.
     *
     * @param tenantId The tenant ID for which the connection is being created.
     * @param authValuesMap A map containing authentication values like access token, refresh token, etc.
     * @return ZohoConnection The newly created connection for the tenant.
     */
    private ZohoConnection createNewConnection(String tenantId, Map<String, Object> authValuesMap, PickListUserAuth userAuth) {
        String accessToken = (String) authValuesMap.get("access_token");
        String refreshToken = (String) authValuesMap.get("refresh_token");
        String scope = (String) authValuesMap.get("scope");
        Integer duration = (Integer) authValuesMap.get("expires_in");

        log.info(authValuesMap);

        ZohoConnection conn = ZohoConnection
                .builder()
                .tenantId(tenantId)
                .appName(zohoConnectorProperties.getAppName())
                .accesToken(accessToken)
                .refreshToken(refreshToken)
                .scope(scope)
                .userId(userAuth.getId())
                .userName(userAuth.getUserName())
                .userEmail(userAuth.getUserEmail())
                .createdIn(LocalDateTime.now())
                .expireIn(LocalDateTime.now().plusSeconds(duration.longValue()))
                .build();
        connectionService.saveOrUpdate(conn);
        return conn;
    }
}
