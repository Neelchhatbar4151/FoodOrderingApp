package com.tss.Exception;

public class AuthenticationFailedException extends RuntimeException{
    public AuthenticationFailedException(){
        super("Incorrect Credentials.");
    }
}
