package com.tss.Repository;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.model.Category;
import com.tss.model.FoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Singleton
public class InMemoryFoodItemRepository implements FoodItemRepository{

    private final Map<Integer, FoodItem> foodItems;
    private final Map<Integer, Category> foodCategories;

    private void init(){
        addFoodCategory(new Category("CHINESE"));
        addFoodCategory(new Category("FASTFOOD"));

        addFoodItem(new FoodItem.Builder("MOMOS", 149.99, foodCategories.get(1)).build());
        addFoodItem(new FoodItem.Builder("NOODLES", 99.99, foodCategories.get(1)).build());

        addFoodItem(new FoodItem.Builder("BURGER", 119.99, foodCategories.get(2)).build());
        addFoodItem(new FoodItem.Builder("PIZZA", 199.99, foodCategories.get(2)).build());
    }

    private InMemoryFoodItemRepository(){
        foodItems = new HashMap<>();
        foodCategories = new HashMap<>();

        init();
    }

    @Override
    public void addFoodItem(FoodItem item) {
        foodItems.put(item.id, item);
    }

    @Override
    public boolean removeFoodItem(int id) {
        if(!foodItems.containsKey(id)){
            return false;
        }
        foodItems.remove(id);
        return true;
    }

    @Override
    public FoodItem getFoodItemById(int id) {
        return foodItems.getOrDefault(id, null);
    }

    @Override
    public List<FoodItem> getAllFoodItems(){
        return new ArrayList<>(foodItems.values());
    }

    @Override
    public boolean setFoodAvailability(int id, AvailabilityStatus state) {
        if(!foodItems.containsKey(id)){
            return false;
        }
        foodItems.get(id).setAvailability(state);
        return true;
    }

    @Override
    public void addFoodCategory(Category category) {
        foodCategories.put(category.id, category);
    }

    @Override
    public boolean removeFoodCategory(int id) {
        if(!foodCategories.containsKey(id)){
            return false;
        }
        foodCategories.remove(id);
        return true;
    }

    @Override
    public Category getFoodCategoryById(int id) {
        return foodCategories.getOrDefault(id, null);
    }

    @Override
    public List<Category> getAllFoodCategories() {
        return new ArrayList<>(foodCategories.values());
    }

    public static class Initiator{
        private static final InMemoryFoodItemRepository instance = new InMemoryFoodItemRepository();
    }

    public static InMemoryFoodItemRepository getInstance(){
        return Initiator.instance;
    }
}
