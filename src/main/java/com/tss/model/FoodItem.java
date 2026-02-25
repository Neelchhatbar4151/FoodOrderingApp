package com.tss.model;

import com.tss.Datatype.AvailabilityStatus;
import static com.tss.Utils.Constant.newFoodItemId;

//FoodItem With Builder Pattern
public class FoodItem {
    public final int id;
    public final String name, description;
    public final double price;
    public final Category category;
    private AvailabilityStatus availability;
    private final int preparationTime;
    private final int calories;

    public FoodItem(Builder builder) {
        this.id = newFoodItemId++;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.category = builder.category;
        this.availability = builder.availability;
        this.preparationTime = builder.preparationTime;
        this.calories = builder.calories;
    }

    public FoodItem(FoodItem other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.price = other.price;
        this.category = other.category;
        this.availability = other.availability;
        this.preparationTime = other.preparationTime;
        this.calories = other.calories;
    }

    public void setAvailability(AvailabilityStatus availability){
        this.availability = availability;
    }

    public AvailabilityStatus getAvailability(){
        return availability;
    }

    public int getCalories() {
        return calories;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", availability=" + availability +
                ", preparationTime=" + preparationTime +
                ", calories=" + calories +
                '}';
    }

    public static class Builder {

        // Required fields
        private final String name;
        private final double price;
        private final Category category;

        // Optional fields
        private String description = "";
        private AvailabilityStatus availability = AvailabilityStatus.AVAILABLE;
        private int preparationTime = 0;
        private int calories = 0;

        public Builder(String name, double price, Category category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder availability(AvailabilityStatus availability) {
            this.availability = availability;
            return this;
        }

        public Builder preparationTime(int preparationTime) {
            this.preparationTime = preparationTime;
            return this;
        }

        public Builder calories(int calories) {
            this.calories = calories;
            return this;
        }

        public FoodItem build() {
            return new FoodItem(this);
        }
    }

}
