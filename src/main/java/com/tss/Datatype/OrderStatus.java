package com.tss.Datatype;

import java.io.Serializable;

public enum OrderStatus implements Serializable {
    CREATED, CONFIRMED, PREPARING, WAITING_FOR_DELIVERY_PARTNER, OUT_FOR_DELIVERY, DELIVERED, CANCELLED;
}