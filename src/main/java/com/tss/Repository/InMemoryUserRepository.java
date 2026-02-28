package com.tss.Repository;

import com.tss.Datatype.Role;
import com.tss.model.User.Admin;
import com.tss.model.User.User;

import java.io.*;
import java.util.*;

//Singleton
public class InMemoryUserRepository implements UserRepository, Serializable {
    private static final String filePath = "./Data/userRepo.ser";
    private final Map<Role, Map<String, User>> users;

    private void init(){
        if(!users.containsKey(Role.ADMIN) || users.get(Role.ADMIN).isEmpty()){
            addNewUser(new Admin("Neel", "9275098742", "ABC"));
        }

//
//        addNewUser(new DeliveryPartner("Rohan", "9275098745", "XYZ"));
//        addNewUser(new DeliveryPartner("Dev", "9275098746", "PQR"));
//
//        addNewUser(new Customer("Amit", "9275098743", "ABC"));
//        addNewUser(new Customer("Suresh", "9275098744", "DEF"));

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
        private static final InMemoryUserRepository instance = load();
        private static InMemoryUserRepository load(){
            File file = new File(filePath);

            if (!file.exists() || file.length() == 0) {
                return new InMemoryUserRepository();
            }
            try (ObjectInputStream in =
                         new ObjectInputStream(new FileInputStream(filePath))) {

                InMemoryUserRepository loadedInstance = (InMemoryUserRepository) in.readObject();
                return Objects.requireNonNullElseGet(loadedInstance, InMemoryUserRepository::new);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static InMemoryUserRepository getInstance(){
        return Initiator.instance;
    }

    public void saveState(){
        try(ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(filePath))){
            System.out.println(this.getClass().getSimpleName());
            out.writeObject(this);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
