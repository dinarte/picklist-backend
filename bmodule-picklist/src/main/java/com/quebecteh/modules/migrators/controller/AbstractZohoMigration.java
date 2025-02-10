package com.quebecteh.modules.migrators.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quebecteh.modules.commons.clients.api.zoho.connector.ZohoConnectorProperties;
import com.quebecteh.modules.commons.clients.api.zoho.model.ZohoResponse;
import com.quebecteh.modules.migrators.domain.DestinationData;
import com.quebecteh.modules.migrators.domain.MigrationLog;
import com.quebecteh.modules.migrators.repository.MigrationLogRepository;
import com.quebecteh.modules.migrators.service.JDBCSourceConnectionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public abstract class AbstractZohoMigration {

	final ZohoConnectorProperties connProperties;
	final JDBCSourceConnectionService connectionService;
	final MigrationLogRepository migrationLogRepository;
	private MigrationConfiguration configuration;
	@Value("${migrations.zoho.inventory.organization_id}")
	protected String zohoOrganizationId;
	@Value("${migrations.zoho.inventory.authToken}")
	String zohoAuthToken;
	
	
	LocalDateTime authTokenDataCreated = LocalDateTime.now().minusMinutes(90);
	String authToken;

	private void configure() {
		this.configuration = getCofiguration();
	}
	
	protected abstract MigrationConfiguration getCofiguration();
	
	protected abstract String getSql(List<String> migratedIds);

	protected abstract Map<String, Object> getMappedJson(Map<String, Object> resultMap) ;
	
	protected Map<String, String> adtionalParams = new HashMap<String, String>();

	public void migrate() throws SQLException {
		
		configure();
		
		var migratedIds = migrationLogRepository.findSourceIdsByFilters(configuration.getTenantId(), 
				configuration.sourceAppName, configuration.sourceEntity, configuration.getDestinationApp(), 
				configuration.getDestinationEntity(), "success");
		
		var resultSet =  connectionService.queryToMap(getSql(migratedIds));
		resultSet.forEach( item -> {
			
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					var itemMapped = getMappedJson(item);
					var json = objectMapper.writeValueAsString(itemMapped);
					
					System.out.println("-----------------------------------------------------------------");
					System.out.println(json);
					
					
					var response = post(zohoOrganizationId, getAuthKey(), configuration.getDestinationResource(), json);
					log.info("Response: Code: {}, Message: {}, Body: {} ", response.getCode(), response.getMessage(), response.getBody());
					createMigrationLog(item, response);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				} catch (RuntimeException re) {
					createInternalErrorMigratorLog(item, re.getMessage());
					re.printStackTrace();
				}
			
		} );
		
	}
	
	protected void createInternalErrorMigratorLog(Map<String, Object> item, String message) {
		var migrationLog = MigrationLog.builder()
				.tenantId(configuration.getTenantId())
				.dateTime(LocalDateTime.now())
				.status(MigrationLog.ERROR)
				.sourceAppName(configuration.getSourceAppName())
				.sourceEntity(configuration.getSourceEntity())
				.sourceId(item.get(configuration.getSourceFieldId()).toString())
				.destinationApp(configuration.destinationApp)
				.destinationEntity(configuration.getDestinationEntity())
				.message(message)
				.build();
		migrationLogRepository.save(migrationLog);
	}

	protected void createMigrationLog(Map<String, Object> item, ZohoResponse response) {
		var migrationLog = MigrationLog.builder()
			.tenantId(configuration.getTenantId())
			.dateTime(LocalDateTime.now())
			.sourceAppName(configuration.getSourceAppName())
			.sourceEntity(configuration.getSourceEntity())
			.sourceId(item.get(configuration.getSourceFieldId()).toString())
			.destinationApp(configuration.destinationApp)
			.destinationEntity(configuration.getDestinationEntity())
			.message("Code: " + response.getCode() + ",  Message: " + response.getMessage())
			.build();
		
		if (response.getCode() == 0) {
			migrationLog.setStatus(MigrationLog.SUCCESS);
			@SuppressWarnings("unchecked")
			var bodyMap = (Map<String, Object>) response.getBody();
			var idDestination = (String) bodyMap.get(configuration.getDestinationFieldId());
			
			var destinationData = DestinationData.builder().migrationLog(migrationLog).tenantId(configuration.tenantId).data(bodyMap).build();
			migrationLog.setDestinationData(destinationData);	
			migrationLog.setDestinationId(idDestination);
			migrationLogsOnCreateCallBack();
		} else if (response.getCode() == 57) {
			throw new RuntimeErrorException(null,response.getMessage());
			//throw new RuntimeException(response.getMessage());
		} else {
			migrationLog.setStatus(MigrationLog.ERROR);
		}
		
		migrationLogRepository.save(migrationLog);
	}
	
	protected void migrationLogsOnCreateCallBack() {
		
	}

	protected ZohoResponse post(String organizationId, String authKey, String resource, String body) {
		
		String url = String.format(getApiBaseUrl())
                .concat("/")
                .concat(resource)
                .concat("?organization_id=")
                .concat(organizationId);

		if (!adtionalParams.isEmpty()) {
			 List<String> params = new ArrayList<>();
			 adtionalParams.forEach((k, v) -> {
			     params.add(k + "=" + v);
			 });
			 String extraQuery = params.stream().collect(Collectors.joining("&"));
			 url = url + "&" + extraQuery;
		}
		
		log.info("Executing POST {}", url);
		log.info("Body {}", body);
		
		HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Content-Type", "application/json")
	            .header("Authorization", "Zoho-oauthtoken " + authKey)
	            .POST(HttpRequest.BodyPublishers.ofString(body))
	            .build();
		
		HttpClient client = HttpClient.newHttpClient();
		
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return getObjectMapper(response.body(), configuration.getDestinationEntity());
		
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		
	}

	protected ZohoResponse getObjectMapper(String responseBody, String node) throws JsonProcessingException, JsonMappingException {
	    ObjectMapper objectMapper = JsonMapper
	        .builder()
	        .addModule(new JavaTimeModule())
	        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
	        .build();
	    
	    String body = responseBody.replace("\""+node+"\"", "\"body\"");
	
	    ZohoResponse apiResponse = objectMapper.readValue(body, ZohoResponse.class);
	    return apiResponse;
	}

	protected String getMigratedIdValue(String sourceEntity, Object sourceId, String destinationEntity) {
		var destinationId = migrationLogRepository.getDestinationIdByFilters(configuration.getTenantId(), 
				configuration.sourceAppName, sourceEntity, sourceId.toString(), configuration.getDestinationApp(), 
				destinationEntity, "success");
		
		if (destinationId == null) {
			throw new RuntimeException("Destination Id not found for sourceEntity: " + sourceEntity + ", sourceId: " + sourceId + " and destinationEntity: " + destinationEntity);
		}
		
		return destinationId;
	}
	
	protected MigrationLog getSuccessMigrationLog(String sourceEntity, Object sourceId, String destinationEntity) {
		var migrationLog = migrationLogRepository.findOneByFilters(configuration.getTenantId(), 
				configuration.sourceAppName, sourceEntity, sourceId.toString(), configuration.getDestinationApp(), 
				destinationEntity, "success");	
		
		try {
			return migrationLog.get();
		}catch (Exception e) {
			throw new RuntimeException("MigratuionLog not found for sourceEntity: " + sourceEntity + ", sourceId: " + sourceId + " and destinationEntity: " + destinationEntity);
		}
		
	}
	
	protected List<Map<String, Object>> jsonToMap(String itemsJsonStr) {
		ObjectMapper objectMapper = new ObjectMapper();
	    List<Map<String, Object>> items;
		try {
			items = objectMapper.readValue(
				itemsJsonStr, new TypeReference<List<Map<String, Object>>>() {}
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return items;
	}
	
	protected abstract String getApiBaseUrl();
	
	
protected String getAuthKey() {
		
		String refreshtokenUrl = "https://accounts.zoho.com/oauth/v2/token?refresh_token=1000.04e448caa34f1121c12f434d381ee43a.f735971d6cec4220274cfa2579888abb&client_id=1000.786MVQWEOFKTV2T9X1UYKD8UAOANXO&client_secret=6af1aa4800240393e2ec05213b0a1d741d0f52ad66&redirect_uri=http://zrkgqo.conteige.cloud/bmodule-picklist/zoho/auth/callback&grant_type=refresh_token&state=bave";
		long minutesPassed = Duration.between(authTokenDataCreated, LocalDateTime.now()).toMinutes();
		if (minutesPassed > 55) {
			log.info("Refresh Token");
			authToken = postRefreshToken(refreshtokenUrl);
			authTokenDataCreated = LocalDateTime.now();
		}
		
		return authToken;
		
	}
	
	protected String postRefreshToken(String url) {
		
		
		log.info("Executing POST {}", url);
	
		
		HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.noBody())
	            .build();
		
		HttpClient client = HttpClient.newHttpClient();
		
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());	
			ObjectMapper objectMapper = new ObjectMapper();
	        @SuppressWarnings("unchecked")
			Map<String, Object> map = objectMapper.readValue(response.body(), Map.class);
			return map.get("access_token").toString();
		
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		
	}


}