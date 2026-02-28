package com.tss.Repository;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.model.Category;
import com.tss.model.FoodItem;

import java.io.Serializable;
import java.util.List;

public interface FoodItemRepository extends Serializable {
    void addFoodItem(FoodItem item);
    boolean removeFoodItem(int id);
    FoodItem getFoodItemById(int id);
    boolean setFoodAvailability(int id, AvailabilityStatus state);
    List<FoodItem> getAllFoodItems();

    void addFoodCategory(Category category);
    boolean removeFoodCategory(int id);
    Category getFoodCategoryById(int id);
    List<Category> getAllFoodCategories();
}