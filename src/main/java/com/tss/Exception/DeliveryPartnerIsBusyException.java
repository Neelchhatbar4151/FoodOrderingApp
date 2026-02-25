package com.tss.Exception;

public class DeliveryPartnerIsBusyException extends RuntimeException{
    public DeliveryPartnerIsBusyException(){
        super("This Delivery Partner is not available for delivery.");
    }
}
