package com.tss.Exception;

public class ItemNotAvailableException extends RuntimeException{
    public ItemNotAvailableException(){
        super("This Item is Currently Not Available.");
    }
}
