package com.tss.Payment;

import com.tss.model.Notification;
import com.tss.model.Order;
import com.tss.model.User.Customer;

import static com.tss.Utils.GlobalVariables.inputTaker;
import static com.tss.Utils.Print.info;
import static com.tss.Utils.Print.success;

public class UPI implements PaymentMode {
    private final Order order;
    private final int transactionReferenceId ;

    private static int newUpiTransactionReferenceNumber = 1;

    public UPI(Order order){
        transactionReferenceId = newUpiTransactionReferenceNumber++;
        this.order = order;
    }

    public void pay(){
        Customer customer = order.getCustomer();
        if(customer.getUpiId().isEmpty() || customer.getUpiId() == null){
            info("Enter UPI Id: ");
            String upiId = inputTaker.nextLine();

            customer.setUpiId(upiId);
        }

        success("Using UPI ID: " + customer.getUpiId());
        success("Initiating Transaction Of " + order.getFinalAmount() + " for Order Id: " + order.getId());
        success("Payment Complete !");
        order.setPayment(this);
        notifyCustomer();
    }

    @Override
    public void notifyCustomer() {
        order.getCustomer().addNotification(new Notification("Payment Of " + order.getFinalAmount() + " was Successful, for Order Id: " + order.getId()));
    }

    @Override
    public String getName() {
        return "UPI";
    }

    @Override
    public String getTransactionReferenceId() {
        return String.valueOf(transactionReferenceId);
    }
}