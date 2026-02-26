package com.tss.Exception;

public class EmptyCartException extends RuntimeException{
    public EmptyCartException(){
        super("Cart Is Empty.");
    }
}
