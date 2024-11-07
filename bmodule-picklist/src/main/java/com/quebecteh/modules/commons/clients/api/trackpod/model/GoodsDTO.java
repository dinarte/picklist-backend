package com.quebecteh.modules.commons.clients.api.trackpod.model;

import java.util.List;

import lombok.Data;

@Data
public class GoodsDTO{

    private Long localID;
    private String orderLineId;
    private String goodsId;
    private String goodsName;
    private String goodsUnit;
    private String note;
    private Double quantity;
    private Double quantityFact;
    private Double cost;
    private String rejectReason;
    private Boolean hasPhoto;
    private List<String> photos;
    private String orderLineBarcode;
    private String goodsBarcode;
    private Boolean scanned;
    private String loadStatus;
}
