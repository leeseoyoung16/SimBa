package com.simba.project01.store;

import lombok.Getter;

@Getter
public class StoreResponse
{
    private Long storeId;
    private String storeName;
    private String storeAddress;
    private String description;
    private String category;
    private String username;

    public StoreResponse(Store store) {
        this.storeId = store.getId();
        this.storeName = store.getName();
        this.storeAddress = store.getLatitude() + ", " + store.getLongitude();
        this.description = store.getDescription();
        this.category = store.getCategory().name();
        this.username = store.getUser().getUsername();
    }

}
