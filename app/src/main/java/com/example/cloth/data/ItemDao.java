package com.example.cloth.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM items WHERE category = :category AND subCategory = :subCategory")
    List<Item> getItemsByCategoryAndSubcategory(String category, String subCategory);

    @Insert
    void insert(Item item);

    @Query("SELECT COUNT(*) FROM items")
    int getItemCount();

    @Query("DELETE FROM items")
    void deleteAllItems();
}