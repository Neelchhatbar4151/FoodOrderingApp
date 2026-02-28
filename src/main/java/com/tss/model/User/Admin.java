package com.tss.model.User;

import com.tss.Datatype.Role;

import java.io.Serializable;

public class Admin extends User implements Serializable {
    public Admin(String name, String phone, String password) {
        super(name, phone, password, Role.ADMIN);
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s %-15s %-20s",
                role, phone, name, createdOn);
    }
}