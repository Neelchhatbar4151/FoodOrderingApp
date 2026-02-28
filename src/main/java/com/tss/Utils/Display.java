package com.tss.Utils;

import com.tss.model.Order;
import com.tss.model.OrderItem;

import java.util.List;

import static com.tss.Utils.Print.*;

public class Display {
    private Display(){}

//    public static void displayAdminMenu() {
//        info("""
//            ================== ADMIN MENU ==================
//            6.  Create New Discount (NS)
//            7.  Create New Flat Discount (NS)
//            8.  Create New Percentage Discount (NS)
//            9.  Create New Festival Discount (NS)
//            12. Notify All Customers
//            13. Notify All Delivery Partners
//            0.  Go Back
//            ================================================
//            Enter your choice:""");
//    }
    public static void displayAdminMenu() {
        info("""
                ================== ADMIN MENU ==================
                1. Manage Food Items
                2. Manage Delivery Partners
                3. Send Notifications
                4. View
                
                0. Go Back
                ================================================
                Enter your choice:""");
    }

    public static void displayManageFoodItemsMenu(){
        info("""
                ================== Manage Food Items ==================
                1. Add New Food Item
                2. Remove Food Item
                
                3. Add New Food Category
                4. Remove Food Category
                
                5. Toggle Food Item Availability
                
                0. Go Back
                =======================================================
                Enter your choice:""");
    }

    public static void displayManageDeliveryPartnersMenu(){
        info("""
                ================== Manage Delivery Partners ==================
                1. Approve Delivery Partner
                2. Unapprove Delivery Partner
                
                3. Change Delivery Partner Commission
                
                0. Go Back
                ==============================================================
                Enter your choice:""");
    }

    public static void displaySendNotificationMenu(){
        info("""
                ================== Manage Delivery Partners ==================
                1. Notify All Customers
                
                2. Notify All Delivery Partners
                
                0. Go Back
                ==============================================================
                Enter your choice:""");
    }

    public static void displayViewMenu(){
        info("""
                ================== View ==================
                1. View Food Items
                
                2. View Food Categories
                
                3. View Delivery Partners
                
                4. View Queues
                
                0. Go Back
                ==========================================
                Enter your choice:""");
    }

    public static void displayMainMenu(){
        info("""
            =========== MAIN MENU ===========
            Registration:
                1. Customer
                2. Delivery Partner
            Login:
                3. Admin
                4. Customer
                5. Delivery Partner
            0. Close App
            =================================
            Enter your choice:""");
    }

    public static void displayCustomerMenu() {
        //Cancel Order is Remaining
        info("""
            ================== CUSTOMER MENU ==================
            1. Add Food Item To Cart
            2. Remove Food Item From Cart
            3. Place Order
            4. Show Cart
            5. Show Order History
            6. Show New Notifications
            7. Show All Notifications
            8. Show On Going Orders
            0.  Go Back
            ===================================================
            Enter your choice:""");
    }

    public static void displayDeliveryPartnerMenu() {
//        1. Set Availability Status (NS) Remaining
        info("""
            ================== Delivery Partner MENU ==================
            1. Complete Order Delivery
            2. Get Total Earnings
            3. Show Orders Delivered
            4. Show New Notifications
            5. Show All Notifications
            0.  Go Back
            ===========================================================
            Enter your choice:""");
    }

    public static void displayPaymentMenu() {
        info("""
            Choose Payment Mode:
            1. UPI
            2. Cash On Delivery
            3. Cancel Order
            Enter:""");
    }

    public static void displayDeliveryPartnerHeading() {
        System.out.printf(
                "%5s %-20s %-15s %-15s %-12s %-20s %-20s %-12s\n",
                "Id",
                "Role",
                "Phone",
                "Name",
                "Earnings",
                "Approved",
                "Created On",
                "Status"
        );

        System.out.println("-".repeat(125));
    }

    public static void displayFoodItemHeading(){
        System.out.printf(
                "%-5s %-20s %-10s %-15s %-15s %-18s %-10s %-30s\n",
                "ID",
                "Name",
                "Price",
                "Category",
                "Available",
                "PrepTime(min)",
                "Calories",
                "Description"
        );

        System.out.println("-".repeat(130));
    }

    public static void displayFoodCategoryHeading() {
        System.out.printf(
                "%-5s %-20s\n",
                "ID",
                "Category Name"
        );

        System.out.println("-".repeat(25));
    }

    public static void displayOrderHeading(){
        System.out.printf(
                "%-8s %-15s %-18s %-25s %-12s %-10s %-12s %-30s %-15s %-20s %-25s\n",
                "OrderID",
                "Payment",
                "TransactionID",
                "DeliveryPartner",
                "Total",
                "Discount",
                "Final",
                "Status",
                "Customer",
                "PlacedOn",
                "DiscountDesc"
        );

        System.out.println("-".repeat(190));
    }

    public static void displayOrderItemHeading(){
        System.out.printf(
                "%-25s %-10s %-12s %-12s\n",
                "FoodItem",
                "Price",
                "Quantity",
                "Subtotal"
        );

        System.out.println("-".repeat(63));
    }

    public static void displayOrder(Order order){
        if(order.getItems().isEmpty()){
            failure("No items in cart...");
            return;
        }
        String discountDescription = order.getDiscountDescription();
        success("Customer: " + order.getCustomer() + " ( " + order.getCustomer().getPhone() + " ) ");
        success("Discount Description: " + (discountDescription == null?"No Discount Applied.":discountDescription));
        Display.displayOrderItemHeading();

        for(OrderItem item: order.getItems()){
            success(item.toString());
        }
        System.out.println("-".repeat(63));
        order.getFinalAmount();
        System.out.printf(
                "%-25s %-10s %-12s %-12.2f\n",
                "",
                "",
                "Discount: ",
                order.getDiscount());
        System.out.printf(
                "%-25s %-10s %-12s %-12.2f\n",
                "",
                "",
                "Total: ",
                order.getFinalAmount());
    }
}
