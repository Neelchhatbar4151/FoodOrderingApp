package com.tss.Repository;

import com.tss.Datatype.Role;
import com.tss.model.User.Admin;
import com.tss.model.User.Customer;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

import java.io.*;
import java.util.*;

//Singleton
public class InMemoryUserRepository implements UserRepository {
    private final Map<Role, Map<String, User>> users;

    private void init(){
        if(!users.containsKey(Role.ADMIN) || users.get(Role.ADMIN).isEmpty()){
            addNewUser(new Admin("Neel", "9275098742", "ABC"));
        }


        addNewUser(new DeliveryPartner("Rohan", "9275098745", "XYZ"));
        addNewUser(new DeliveryPartner("Dev", "9275098746", "PQR"));

        addNewUser(new Customer("Amit", "9275098743", "ABC"));
        addNewUser(new Customer("Suresh", "9275098744", "DEF"));

    }

    private InMemoryUserRepository(){
        this.users = new HashMap<>();

        init();
    }

    @Override
    public User getUser(String phone, String password, Role role) {
        Map<String, User> roleSpecificUsers = users.getOrDefault(role, null);
        if(roleSpecificUsers == null || !roleSpecificUsers.containsKey(phone)){
            return null;
        }
        User user = roleSpecificUsers.get(phone);
        return user.matchPassword(password) ? user : null;
    }

    @Override
    public List<User> getAllUsersInRole(Role role){
        if(!users.containsKey(role)){
            return new ArrayList<>();
        }
        return new ArrayList<>(users.get(role).values());
    }

    @Override
    public User getUserById(int id) {
        for(Map<String, User> map: users.values()){
            for(User u: map.values()){
                if(id == u.id){
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public boolean addNewUser(User user) {
        Map<String, User> roleSpecificUsers = users.getOrDefault(user.role, new HashMap<>());
        if(roleSpecificUsers.containsKey(user.getPhone())){
            return false;
        }
        roleSpecificUsers.put(user.getPhone(), user);
        users.put(user.role, roleSpecificUsers);
        return true;
    }

    public static class Initiator{
        private static final InMemoryUserRepository instance = new InMemoryUserRepository();
    }

    public static InMemoryUserRepository getInstance(){
        return Initiator.instance;
    }
}
