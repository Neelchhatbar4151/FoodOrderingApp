package com.tss.Service;


import com.tss.model.CurrentUser;
import com.tss.model.User.DeliveryPartner;

public class DeliveryService {

//    private final UserRepository userRepo;
    private final DeliveryPartner deliveryPartner;

    public DeliveryService(){
        deliveryPartner = (DeliveryPartner) CurrentUser.getInstance().getUser();
//        this.userRepo = userRepo;
    }

    public void start(){

    }
}
