package com.quebecteh.modules.commons.clients.api.zoho.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ZohoApiClientSalesOrderDTO {

    @JsonProperty("salesorder_id")
    private Long salesOrderId;

    @JsonProperty("salesorder_number")
    private String salesOrderNumber;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("status")
    private String status;

    @JsonProperty("shipment_date")
    private LocalDate shipmentDate;

    @JsonProperty("reference_number")
    private String referenceNumber;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("currency_id")
    private Long currencyId;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("currency_symbol")
    private String currencySymbol;

    @JsonProperty("exchange_rate")
    private BigDecimal exchangeRate;

    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;

    @JsonProperty("discount")
    private String discount;

    @JsonProperty("is_discount_before_tax")
    private boolean isDiscountBeforeTax;

    @JsonProperty("discount_type")
    private String discountType;

    @JsonProperty("estimate_id")
    private Long estimateId;

    @JsonProperty("delivery_method")
    private String deliveryMethod;

    @JsonProperty("delivery_method_id")
    private Long deliveryMethodId;

    @JsonProperty("is_inclusive_tax")
    private boolean isInclusiveTax;

    @JsonProperty("sales_channel")
    private String salesChannel;

    @JsonProperty("is_dropshipped")
    private boolean isDropshipped;

    @JsonProperty("is_backordered")
    private boolean isBackordered;

    @JsonProperty("is_backorder_allowed")
    private boolean isBackorderAllowed;

    @JsonProperty("shipping_charge")
    private BigDecimal shippingCharge;

    @JsonProperty("adjustment")
    private BigDecimal adjustment;

    @JsonProperty("pricebook_id")
    private Long pricebookId;

    @JsonProperty("adjustment_description")
    private String adjustmentDescription;

    @JsonProperty("sub_total")
    private BigDecimal subTotal;

    @JsonProperty("tax_total")
    private BigDecimal taxTotal;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("price_precision")
    private int pricePrecision;

    @JsonProperty("is_emailed")
    private boolean isEmailed;

    @JsonProperty("has_unconfirmed_line_item")
    private boolean hasUnconfirmedLineItem;
    
    @JsonProperty("purchaseorders")
    private List<String> purchaseOrders;

    @JsonProperty("billing_address")
    private ZohoApiClientAddressDTO billingAddress;

    @JsonProperty("shipping_address")
    private ZohoApiClientAddressDTO shippingAddress;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("terms")
    private String terms;

    @JsonProperty("template_id")
    private Long templateId;

    @JsonProperty("template_name")
    private String templateName;

    @JsonProperty("template_type")
    private String templateType;

    @JsonProperty("created_time")
    private OffsetDateTime createdTime;

    @JsonProperty("last_modified_time")
    private OffsetDateTime lastModifiedTime;

    @JsonProperty("attachment_name")
    private String attachmentName;

    @JsonProperty("can_send_in_mail")
    private boolean canSendInMail;

    @JsonProperty("salesperson_id")
    private Long salespersonId;

    @JsonProperty("salesperson_name")
    private String salespersonName;

    @JsonProperty("is_pre_gst")
    private boolean isPreGst;

    @JsonProperty("gst_no")
    private String gstNo;

    @JsonProperty("gst_treatment")
    private String gstTreatment;

    @JsonProperty("place_of_supply")
    private String placeOfSupply;

    @JsonProperty("documents")
    private List<ZohoApiClientDocumentDTO> documents;
    
    @JsonProperty("contact_persons")
    private List<String> contactPersons;
    
    @JsonProperty("contact_persons_associated")
    private List<ZohoApiClientContactPersonDTO> contactPersonsAssociated;
    
    @JsonProperty("line_items")
    private List<ZohoApiClientLineItemDTO> lineItems;
    
    @JsonProperty("packages")
    private List<ZohoApiClientPackageDTO> packages;

    @JsonProperty("invoices")
    private List<ZohoApiClientInvoiceDTO> invoices;
    
    @JsonProperty("custom_fields")
    private List<ZohoApiClientCustomFieldDTO> customFields;
    
    @JsonProperty("taxes")
    private List<ZohoApiClientTaxDTO> taxes;

}
