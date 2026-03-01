package com.tss.Payment;

import com.tss.model.Notification;
import com.tss.model.Order;

import static com.tss.Utils.Print.success;

public class CashOnDelivery implements PaymentMode {
    private final Order order;
    public CashOnDelivery(Order order){
        this.order = order;
    }

    @Override
    public void pay() {
        success("Delivery Partner will take payment upon Delivery.");
        order.setPayment(this);
        notifyCustomer();
    }

    @Override
    public void notifyCustomer() {
        order.getCustomer().addNotification(new Notification("Payment Mode Selected as Cash On Delivery, For Amount:  " + order.getFinalAmount() + ", For Order Id: " + order.getId()));
    }

    @Override
    public String getName() {
        return "COD";
    }

    @Override
    public String getTransactionReferenceId() {
        return null;
    }
}
