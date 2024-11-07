package com.quebecteh.modules.commons.connector.service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quebecteh.commons.multitenancy.AbstractMultiTenancyCrudService;
import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorService;
import com.quebecteh.modules.commons.connector.model.domain.ZohoConnection;

import lombok.SneakyThrows;

@Service
public class ZohoConnectionServiceImpl	extends AbstractMultiTenancyCrudService<ZohoConnection, Long> 
										implements ZohoConnectionService  {

	@Autowired
    private ZohoConnectorService zohoConnectorService;

    @SneakyThrows
    public void renewConnectionIsExipered(ZohoConnection conn) {
        
    	conn = findById(conn.getId()).get();
    	
    	if (conn.getExpireIn().isBefore(LocalDateTime.now().plusMinutes(10))) {
            Map<String, Object> authValuesMap = zohoConnectorService.sendPostRenewAuth(conn.getTenantId(), conn.getRefreshToken());
            String accessToken = (String) authValuesMap.get("access_token");
            String refreshToken = (String) authValuesMap.get("refresh_token");
            String scope = (String) authValuesMap.get("scope");
            Integer duration = (Integer) authValuesMap.get("expires_in");

            if (accessToken != null)
            	conn.setAccesToken(accessToken);
            
            conn.setScope(scope);
            conn.setRenewdIn(LocalDateTime.now());
            conn.setExpireIn(LocalDateTime.now().plusSeconds(duration.longValue()));

            if (Objects.nonNull(refreshToken)) {
                conn.setRefreshToken(refreshToken);
            }
            
            super.saveOrUpdate(conn);
        }
    }
    
	
}
