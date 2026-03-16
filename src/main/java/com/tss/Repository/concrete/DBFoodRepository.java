package com.tss.Repository.concrete;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Repository.FoodItemRepository;
import com.tss.model.Category;
import com.tss.model.FoodItem;

import java.util.List;

public class DBFoodRepository implements FoodItemRepository {



    @Override
    public void addFoodItem(FoodItem item) {

    }

    @Override
    public boolean removeFoodItem(int id) {
        return false;
    }

    @Override
    public FoodItem getFoodItemById(int id) {
        return null;
    }

    @Override
    public boolean setFoodAvailability(int id, AvailabilityStatus state) {
        return false;
    }

    @Override
    public List<FoodItem> getAllFoodItems() {
        return List.of();
    }

    @Override
    public void addFoodCategory(Category category) {

    }

    @Override
    public boolean removeFoodCategory(int id) {
        return false;
    }

    @Override
    public Category getFoodCategoryById(int id) {
        return null;
    }

    @Override
    public List<Category> getAllFoodCategories() {
        return List.of();
    }
}
