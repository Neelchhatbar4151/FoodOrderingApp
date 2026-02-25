package com.tss.Repository;

import com.tss.Datatype.Role;
import com.tss.model.User.Admin;
import com.tss.model.User.DeliveryPartner;
import com.tss.model.User.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Role, Map<String, User>> users;

    public InMemoryUserRepository(){
        this.users = new HashMap<>();

        //Default Accounts
        users.put(Role.ADMIN, new HashMap<>());
        users.get(Role.ADMIN).put("1234", new Admin("Neel", "1234", "BCD"));
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
    public void addNewUser(User user) {
        Map<String, User> roleSpecificUsers = users.getOrDefault(user.role, new HashMap<>());
        roleSpecificUsers.put(user.getPhone(), user);
        users.put(user.role, roleSpecificUsers);
    }
}
