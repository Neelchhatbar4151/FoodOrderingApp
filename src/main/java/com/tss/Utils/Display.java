package com.tss.Utils;

import java.util.List;

import static com.tss.Utils.Print.info;
import static com.tss.Utils.Print.success;

public class Display {
    private Display(){}

    public static void displayAdminMenu() {
        info("""
            ================== ADMIN MENU ==================
            1.  Add New Food Item
            2.  Add New Food Category
            3.  Remove Food Item
            4.  Remove Food Category
            5.  Toggle Food Item Availability
            6.  Create New Discount (NS)
            7.  Create New Flat Discount (NS)
            8.  Create New Percentage Discount (NS)
            9.  Create New Festival Discount (NS)
            10. Approve Delivery Partner
            11. Unapprove Delivery Partner
            12. Notify All Customers
            13. Notify All Delivery Partners
            14. Display All Food Items
            15. Display All Food Categories
            16. Change Delivery Partner Commission
            17. Display All Delivery Partners
            0.  Go Back
            ================================================
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
        info("""
            ================== CUSTOMER MENU ==================
            1. Add Food Item To Cart
            2. Remove Food Item From Cart
            3. Place Order
            4. Show Cart
            5. Cancel Order (NS)
            6. Show Order History
            7. Show New Notifications
            8. Show All Notifications
            9. Show On Going Orders
            0.  Go Back
            ================================================
            Enter your choice:""");
    }

    public static void displayDeliveryPartnerMenu() {
        info("""
            ================== CUSTOMER MENU ==================
            1. Set Availability Status (NS)
            2. Complete Order Delivery
            3. Get Total Earnings
            4. Show Orders Delivered
            5. Show New Notifications
            6. Show All Notifications
            0.  Go Back
            ================================================
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
}
