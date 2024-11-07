package com.quebecteh.modules.commons.connector.model.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quebecteh.modules.inventary.picklist.model.domain.PickListUserAuth;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PickListUserAuthConverter implements AttributeConverter<PickListUserAuth, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(PickListUserAuth pickListUserAuth) {
        try {
            return objectMapper.writeValueAsString(pickListUserAuth);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao converter PickListUserAuth para JSON", e);
        }
    }

    @Override
    public PickListUserAuth convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, PickListUserAuth.class);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException("Erro ao mapear JSON para PickListUserAuth", e);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao processar JSON", e);
        }
    }
}