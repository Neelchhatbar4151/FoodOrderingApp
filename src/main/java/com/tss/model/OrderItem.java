package com.tss.model;

public class OrderItem {
    public FoodItem foodItem;
    private int quantity;

    public OrderItem(FoodItem foodItem){
        this.foodItem = foodItem;
        this.quantity = 1;
    }

    public void increaseQuantity(){
        quantity++;
    }

    //return false if quantity becomes 0
    public boolean decreaseQuantity(){
        quantity--;
        return (quantity > 0);
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
                foodItem.name + " ( ID: " + foodItem.id + " )",
                foodItem.price,
                quantity,
                getSubTotal()
        );
    }
}
