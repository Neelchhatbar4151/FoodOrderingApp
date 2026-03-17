package com.tss.model;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Utils.GlobalVariables;

//FoodItem With Builder Pattern
public class FoodItem {
    public final int id;
    public final String name, description;
    public final double price;
    public Category category;
    private AvailabilityStatus availability;
    private final int preparationTime;
    private final int calories;

    public FoodItem(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.category = builder.category;
        this.availability = builder.availability;
        this.preparationTime = builder.preparationTime;
        this.calories = builder.calories;
    }

    public void setAvailability(AvailabilityStatus availability){
        this.availability = availability;
    }

    public AvailabilityStatus getAvailability(){
        return availability;
    }

    @Override
    public String toString() {
        return String.format(
                "%-5d %-20s %-10.2f %-15s %-15s %-18s %-10s %-30s",
                id,
                name,
                price,
                (category == null?"-":category.name),
                availability,
                (preparationTime == 0?"-":preparationTime),
                (calories == 0?"-":calories),
                (description.isEmpty()?"-":description)
        );
    }

    public static class Builder {

        private int id;

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

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public FoodItem build() {
            return new FoodItem(this);
        }
    }

}
