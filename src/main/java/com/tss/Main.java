package com.tss;

import com.tss.Repository.InMemoryFoodItemRepository;
import com.tss.Repository.InMemoryUserRepository;
import com.tss.Service.OrderService;
import com.tss.Utils.GlobalVariables;

public class Main {
    public static void main(String[] args) {
        new MiniFoodOrderingApp().start();

//        InMemoryUserRepository.getInstance().saveState();
//        InMemoryFoodItemRepository.getInstance().saveState();
//        OrderService.getInstance().saveState();
//        GlobalVariables.getInstance().saveState();
    }
}