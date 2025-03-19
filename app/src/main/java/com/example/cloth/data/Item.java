package com.example.cloth.data; // Adjust the package name based on your project structure

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id; // Primary key, automatically generated

    public String name; // Item name
    public String category; // Main category: Women, Men, Kids
    public String subCategory; // Subcategory: Casual, Formal, etc.
    public String image; // Store image as byte array
    public double price; // Item price
    public String availableColors; // Available colors (comma-separated string)
    public int stockCount; // Number of items in stock

    // You can add constructors, getters, and setters if needed
    public Item(String name, String category, String subCategory, String image, double price, String availableColors, int stockCount) {
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.image = image;
        this.price = price;
        this.availableColors = availableColors;
        this.stockCount = stockCount;
    }

    // Default constructor
    public Item() {
    }
}
