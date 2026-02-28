package com.tss.Payment;

import java.io.Serializable;

public interface PaymentMode extends Serializable {
    void pay();
    void notifyCustomer();
    String getName();
    String getTransactionReferenceId();
}
