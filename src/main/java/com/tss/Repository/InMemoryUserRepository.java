package com.tss.Repository;

import com.tss.Datatype.Role;
import com.tss.model.User.Admin;
import com.tss.model.User.Customer;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Role, Map<String, User>> users;

    private void init(){
        addNewUser(new Admin("Neel", "1234", "ABC"));

        addNewUser(new DeliveryPartner("Rohan", "9876", "XYZ"));
        addNewUser(new DeliveryPartner("Mohan", "5678", "PQR"));

        addNewUser(new Customer("Amit", "9123", "ABC"));
        addNewUser(new Customer("Suresh", "3456", "DEF"));
    }

    public InMemoryUserRepository(){
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
        return user.getPassword().equals(password) ? user : null;
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
}
