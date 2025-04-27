package com.ql.BlogApplication.DTO;

import lombok.Data;

@Data
public class SubscriptionDto {
    private String userId;
    private String authorId;
    private boolean isSubscribed;

    public boolean isSubscribed() {
        return isSubscribed;
    }
//
//    public void setIsSubscribed(boolean isSubscribed) {
//        this.isSubscribed = isSubscribed;
//    }
}
