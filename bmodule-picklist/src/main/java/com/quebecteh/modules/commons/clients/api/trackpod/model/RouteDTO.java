package com.quebecteh.modules.commons.clients.api.trackpod.model;

import java.util.List;

import lombok.Data;

@Data
public class RouteDTO {

    private String code;
    private String id;
    private String date;
    private String depotId;
    private String depot;
    private boolean startFromDepot;
    private boolean returnToDepot;
    private String driverLogin;
    private String driverName;
    private int driverNumber;
    private String driverVehicle;
    private String startDate;
    private String closeDate;
    private double track;
    private int priority;
    private double locationLat;
    private double locationLon;
    private String startTimePlan;
    private String finishTimePlan;
    private int distancePlan;
    private String createDateUtc;
    private List<OrderDTO> orders;
    private String status;
    private boolean xd;
    private VehicleDTO vehicle;
    private List<CustomFieldDTO> customFields;

}
