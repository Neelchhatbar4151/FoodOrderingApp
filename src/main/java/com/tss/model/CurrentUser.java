package com.tss.model;

import com.tss.model.User.User;

public class CurrentUser {
    private User user;

    private CurrentUser(){
        user = null;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    private static class Initiator{
        private static final CurrentUser instance = new CurrentUser();
    }

    public static CurrentUser getInstance(){
        return Initiator.instance;
    }

}
