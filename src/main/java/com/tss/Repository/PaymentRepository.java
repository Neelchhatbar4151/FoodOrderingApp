package com.tss.Repository;

public interface PaymentRepository {
    long createPayment(String paymentMode, String transactionReferenceId);
    PaymentRecord getById(long paymentId);
    boolean updatePayment(long paymentId, String paymentMode, String transactionReferenceId);

    record PaymentRecord(long id, String paymentMode, String transactionReferenceId) {}
}
