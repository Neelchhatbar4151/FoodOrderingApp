package com.tss.Repository;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.model.Category;
import com.tss.model.FoodItem;

import java.io.*;
import java.util.*;

//Singleton
public class InMemoryFoodItemRepository implements FoodItemRepository, Serializable{

    private final Map<Integer, FoodItem> foodItems;
    private final Map<Integer, Category> foodCategories;

    private static final String filePath = "./Data/foodRepo.ser";

    private void init(){
//        addFoodCategory(new Category("CHINESE"));
//        addFoodCategory(new Category("FASTFOOD"));
//
//        addFoodItem(new FoodItem.Builder("MOMOS", 149.99, foodCategories.get(1)).build());
//        addFoodItem(new FoodItem.Builder("NOODLES", 99.99, foodCategories.get(1)).build());
//
//        addFoodItem(new FoodItem.Builder("BURGER", 119.99, foodCategories.get(2)).build());
//        addFoodItem(new FoodItem.Builder("PIZZA", 199.99, foodCategories.get(2)).build());
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
        private static final InMemoryFoodItemRepository instance = load();
        private static InMemoryFoodItemRepository load(){
            File file = new File(filePath);

            if (!file.exists() || file.length() == 0) {
                return new InMemoryFoodItemRepository();
            }
            try (ObjectInputStream in =
                         new ObjectInputStream(new FileInputStream(filePath))) {

                InMemoryFoodItemRepository loadedInstance = (InMemoryFoodItemRepository) in.readObject();
                return Objects.requireNonNullElseGet(loadedInstance, InMemoryFoodItemRepository::new);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static InMemoryFoodItemRepository getInstance(){
        return Initiator.instance;
    }

    public void saveState(){
        try(ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(filePath))){
            out.writeObject(this);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
