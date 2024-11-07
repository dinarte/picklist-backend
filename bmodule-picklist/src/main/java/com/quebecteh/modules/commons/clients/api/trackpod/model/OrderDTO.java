package com.quebecteh.modules.commons.clients.api.trackpod.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderDTO {

    private String number;
    private String id;
    private LocalDateTime date;
    private Integer seqNumber;
    private String routeNumber;
    private LocalDateTime routeDate;
    private Integer routePriority;
    private String routeStatus;
    private String driverLogin;
    private String driverName;
    private Integer driverNumber;
    private String driverVehicle;
    private Integer type;
    private String shipperId;
    private String shipper;
    private String depotId;
    private String depot;
    private String clientId;
    private String client;
    private String addressId;
    private String address;
    private Double addressLat;
    private Double addressLon;
    private String addressZone;
    private LocalDateTime timeSlotFrom;
    private LocalDateTime timeSlotTo;
    private Double serviceTime;
    private String note;
    private String contactName;
    private String phone;
    private String email;
    private Double weight;
    private Double volume;
    private Double pallets;
    private Double cod;
    private String codActual;
    private Integer statusId;
    private String status;
    private Double statusLat;
    private Double statusLon;
    private String driverComment;
    private String rejectReason;
    private String signatureName;
    private Boolean hasSignaturePhoto;
    private List<String> signaturePhotos;
    private Boolean hasPhoto;
    private List<String> photos;
    private LocalDateTime statusDate;
    private LocalDateTime eta;
    private LocalDateTime updatedEta;
    private LocalDateTime arrivedDate;
    private LocalDateTime departedDate;
    private String invoiceId;
    private String customerReferenceId;
    private List<GoodsDTO> goodsList;
    private String reportUrl;
    private List<CustomFieldDTO> customFields;
    private String barcode;
    private Boolean scanned;
    private Double feedbackRating;
    private String trackKey;
    private String trackId;
    private String trackLink;
    private String loadStatus;
    private LocalDateTime loadDate;
    private List<String> loadSignaturePhotos;
    private LocalDateTime changeDate;
    private Integer pickupOrderIdDocShipment;
    private Integer pickupOrderType;
    private Integer loadStatusId;
    private Boolean hasLoadSignature;
    private String customFieldsStr;
    private Integer cancelledStatus;
    private String createSource;
    private Double distanceFromDepotPlan;
    private Integer seqNumberDriver;
    private String deliveryInstructions;
}
    

