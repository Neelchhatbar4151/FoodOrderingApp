package com.tss.model;

import com.tss.Datatype.AvailabilityStatus;

public class OrderItem {
    public long id;
    public FoodItem foodItem;
    private int quantity;

    public OrderItem(FoodItem foodItem, int quantity){
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public OrderItem(long id, FoodItem foodItem, int quantity){
        this.id = id;
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public void increaseQuantity(int quantity){
        this.quantity += quantity;
    }

    //return false if quantity becomes 0
    public boolean decreaseQuantity(int quantity){
        if(this.quantity < quantity){
            throw new IllegalArgumentException("Invalid Quantity.");
        }
        this.quantity -= quantity;
        return (this.quantity == 0);
    }

    public int getCurrentQuantity(){
        return quantity;
    }

    public double getSubTotal(){
        return quantity * foodItem.price;
    }

    @Override
    public String toString() {

        return String.format(
                "%-25s %-10.2f %-12d %-12.2f",
                foodItem.name +
                        " ( ID: " + foodItem.id +
                        (foodItem.getAvailability() == AvailabilityStatus.NOT_AVAILABLE?", Unavailable":"") + " )",
                foodItem.price,
                quantity,
                getSubTotal()
        );
    }
}
