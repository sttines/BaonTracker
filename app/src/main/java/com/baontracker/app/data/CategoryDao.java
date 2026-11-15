package com.baontracker.app.data;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY category_id")
    List<Category> getAll();
}
