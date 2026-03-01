package com.tss.Datatype;

import com.tss.Service.AdminService;
import com.tss.Service.CustomerService;
import com.tss.Service.DeliveryPartnerService;
import com.tss.model.User.Admin;
import com.tss.model.User.Customer;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

public enum Role {

    CUSTOMER {
        @Override
        public User createUser(String name, String phone, String password) {
            return new Customer(name, phone, password);
        }

        @Override
        public void startService() {
            new CustomerService().start();
        }
    },

    ADMIN {
        @Override
        public User createUser(String name, String phone, String password) {
            return new Admin(name, phone, password);
        }

        @Override
        public void startService() {
            new AdminService().start();
        }
    },

    DELIVERY_PARTNER {
        @Override
        public User createUser(String name, String phone, String password) {
            return new DeliveryPartner(name, phone, password);
        }

        @Override
        public void startService() {
            new DeliveryPartnerService().start();
        }
    };

    public abstract User createUser(String name, String phone, String password);
    public abstract void startService();
}