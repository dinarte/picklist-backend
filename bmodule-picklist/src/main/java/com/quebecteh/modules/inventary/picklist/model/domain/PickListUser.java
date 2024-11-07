package com.quebecteh.modules.inventary.picklist.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quebecteh.modules.commons.accounts.converters.EncryptionConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table( name = "user", schema = "bmodules-picklist")
public class PickListUser {
	
	@JsonProperty("id")
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String name;
	
	@NotEmpty @Email
	private String email;
	
	@JsonIgnore
	@NotEmpty @Convert(converter = EncryptionConverter.class)
	private String password;
	
	@NotEmpty
	private String roles;
	
	@NotEmpty
	private String tenantId;
	
	private String originalId;

}
