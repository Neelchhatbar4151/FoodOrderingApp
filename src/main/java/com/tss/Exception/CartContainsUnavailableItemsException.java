package com.tss.Exception;

public class CartContainsUnavailableItemsException extends RuntimeException{
    public CartContainsUnavailableItemsException(){
        super("Cart Contains Some Unavailable Items, Please Remove them to place order...");
    }
}
