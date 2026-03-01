package com.tss.Repository;

import com.tss.Datatype.Role;
import com.tss.model.User.User;

import java.util.List;

public interface UserRepository {
    User getUser(String phone, String password, Role role);
    boolean addNewUser(User user);
    List<User> getAllUsersInRole(Role role);
    User getUserById(int id);
}
