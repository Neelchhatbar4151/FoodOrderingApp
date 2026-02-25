package com.tss.model.User;

import com.tss.Datatype.Role;

public class Admin extends User{
    public Admin(String name, String phone, String password) {
        super(name, phone, password, Role.ADMIN);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", notifications=" + notifications +
                ", role=" + role +
                ", createdOn=" + createdOn +
                '}';
    }
}
