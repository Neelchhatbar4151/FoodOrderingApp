package com.tss.Service;


import com.tss.Repository.UserRepository;
import com.tss.model.CurrentUser;
import com.tss.model.User.Customer;

public class CustomerService {

    private final UserRepository userRepo;
    private final Customer customer;

    public CustomerService(UserRepository userRepo){
        customer = (Customer) CurrentUser.getInstance().getUser();
        this.userRepo = userRepo;
    }

    public void start(){

    }
}
