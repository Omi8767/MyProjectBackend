package com.project.backend.dto;

import com.project.backend.entity.ShippingInfo;

import java.util.List;

public class OrderDTO {
    private Long customerId;
    private ShippingInfo shipping;
    private List<OrderItemDTO> items;
    private Double gstPercent;
    private Double discountPercent;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public ShippingInfo getShipping() {
        return shipping;
    }

    public void setShipping(ShippingInfo shipping) {
        this.shipping = shipping;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public Double getGstPercent() {
        return gstPercent;
    }

    public void setGstPercent(Double gstPercent) {
        this.gstPercent = gstPercent;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }
}
