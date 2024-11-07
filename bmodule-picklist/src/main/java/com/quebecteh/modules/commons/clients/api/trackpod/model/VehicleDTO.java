package com.quebecteh.modules.commons.clients.api.trackpod.model;

import lombok.Data;

@Data
public class VehicleDTO {

    private String number;
    private String carrierCode;
    private String carrier;
    private double weight;
    private double volume;
    private int pallets;

}
