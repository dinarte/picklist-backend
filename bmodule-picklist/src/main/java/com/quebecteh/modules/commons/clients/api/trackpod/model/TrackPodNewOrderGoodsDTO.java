package com.quebecteh.modules.commons.clients.api.trackpod.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TrackPodNewOrderGoodsDTO {

    @JsonProperty("OrderLineId")
    private String orderLineId;

    @JsonProperty("GoodsId")
    private String goodsId;

    @JsonProperty("GoodsName")
    private String goodsName;

    @JsonProperty("GoodsUnit")
    private String goodsUnit;

    @JsonProperty("Note")
    private String note;

    @JsonProperty("Quantity")
    private double quantity;

    @JsonProperty("Cost")
    private double cost;

    @JsonProperty("OrderLineBarcode")
    private String orderLineBarcode;

    @JsonProperty("GoodsBarcode")
    private String goodsBarcode;
}
