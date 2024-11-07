package com.quebecteh.modules.commons.clients.api.trackpod.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrackPodNewOrderDTO {

    @JsonProperty("Number")
    private String number;

    @JsonProperty("Id")
    private String id; 

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("Date")
    private LocalDateTime date;

    @JsonProperty("Type")
    private int type;

    @JsonProperty("Shipper")
    private String shipper;

    @JsonProperty("Depot")
    private String depot;

    @JsonProperty("Client")
    private String client;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("AddressLat")
    private double addressLat;

    @JsonProperty("AddressLon")
    private double addressLon;

    @JsonProperty("AddressZone")
    private String addressZone;

    @JsonProperty("TimeSlotFrom")
    private LocalDateTime timeSlotFrom;

    @JsonProperty("TimeSlotTo")
    private LocalDateTime timeSlotTo;

    @JsonProperty("ServiceTime")
    private int serviceTime;

    @JsonProperty("Note")
    private String note;

    @JsonProperty("ContactName")
    private String contactName;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Weight")
    private double weight;

    @JsonProperty("Volume")
    private double volume;

    @JsonProperty("Pallets")
    private double pallets;

    @JsonProperty("COD")
    private double cod;

    @JsonProperty("Barcode")
    private String barcode;

    @JsonProperty("GoodsList")
    private List<TrackPodNewOrderGoodsDTO> goodsList;

    @JsonProperty("CustomFields")
    private List<TrackPodNewOrderCustomFieldDTO> customFields;
}
