package com.simba.project01.store;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class StoreResponse
{
    private Long storeId;
    private String storeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String category;
    private String username;
    private String businessNumber;
    private String address;

    public StoreResponse(Store store) {
        this.storeId = store.getId();
        this.storeName = store.getName();
        this.latitude = store.getLatitude();
        this.longitude = store.getLongitude();
        this.category = store.getCategory().name();
        this.username = store.getUser().getUsername();
        this.businessNumber = store.getBusinessNumber();
        this.address = store.getAddress();
    }

}
