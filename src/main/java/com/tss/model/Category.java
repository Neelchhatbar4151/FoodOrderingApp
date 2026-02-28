package com.tss.model;

import com.tss.Utils.GlobalVariables;

import java.io.Serializable;

public class Category implements Serializable {
    public final int id;
    public final String name;

    public Category(String name){
        this.id = GlobalVariables.getInstance().newCategoryId++;
        this.name = name;
    }

    public Category(Category category){
        this.id = category.id;
        this.name = category.name;
    }

    @Override
    public String toString() {
        return String.format(
                "%-5d %-20s",
                id,
                name
        );
    }
}
