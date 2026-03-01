package com.tss.model.User;

import com.tss.Datatype.Role;

public class Admin extends User {
    public Admin(String name, String phone, String password) {
        super(name, phone, password, Role.ADMIN);
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s %-15s %-20s",
                role, phone, name, createdOn);
    }
}