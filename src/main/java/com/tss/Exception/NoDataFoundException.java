package com.tss.Exception;

public class NoDataFoundException extends RuntimeException{
    public NoDataFoundException(String entity){
        super("No " + entity + " Found.");
    }
}
