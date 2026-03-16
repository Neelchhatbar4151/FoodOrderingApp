package com.tss.Repository.concrete;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Repository.FoodItemRepository;
import com.tss.model.Category;
import com.tss.model.FoodItem;

import java.util.*;

//Singleton
public class InMemoryFoodItemRepository implements FoodItemRepository {

    private final Map<Integer, FoodItem> foodItems;
    private final Map<Integer, Category> foodCategories;

    private void init(){
        addFoodCategory(new Category("CHINESE"));
        addFoodCategory(new Category("FASTFOOD"));
        addFoodCategory(new Category("SOUTH_INDIAN"));
        addFoodCategory(new Category("PUNJABI"));
        addFoodCategory(new Category("GUJARATI"));
        addFoodCategory(new Category("CONTINENTAL"));
        addFoodCategory(new Category("SEAFOOD"));
        addFoodCategory(new Category("VEGAN"));
        addFoodCategory(new Category("DESSERTS"));
        addFoodCategory(new Category("BAKERY"));
        addFoodCategory(new Category("BEVERAGES"));
        addFoodCategory(new Category("STREET_FOOD"));
        addFoodCategory(new Category("HEALTHY"));
        addFoodCategory(new Category("SNACKS"));

        addFoodItem(new FoodItem.Builder("MOMOS", 149.99, foodCategories.get(1))
                .preparationTime(10)
                .description("Steamed Momos")
                .calories(250)
                .build());

        addFoodItem(new FoodItem.Builder("NOODLES", 99.99, foodCategories.get(1))
                .preparationTime(15)
                .calories(400)
                .build());

        addFoodItem(new FoodItem.Builder("BURGER", 119.99, foodCategories.get(2))
                .preparationTime(10)
                .build());

        addFoodItem(new FoodItem.Builder("CHEESE PIZZA", 199.99, foodCategories.get(2))
                .preparationTime(20)
                .calories(850)
                .build());
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
        Category category = getFoodCategoryById(id);
        for(FoodItem item: foodItems.values()){
            if(item.category == category){
                item.category = null;
            }
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
