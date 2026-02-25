package com.tss.Service;


import com.tss.model.CurrentUser;
import com.tss.model.User.DeliveryPartner;

public class DeliveryPartnerService {

//    private final UserRepository userRepo;
    private final DeliveryPartner deliveryPartner;

    public DeliveryPartnerService(){
        deliveryPartner = (DeliveryPartner) CurrentUser.getInstance().getUser();
//        this.userRepo = userRepo;
    }

    public void start(){

    }
}
