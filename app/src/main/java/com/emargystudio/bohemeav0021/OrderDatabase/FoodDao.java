package com.emargystudio.bohemeav0021.OrderDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.emargystudio.bohemeav0021.Model.FoodMenu;
import java.util.ArrayList;
import java.util.List;


@Dao
public interface FoodDao {

    @Query("SELECT * FROM foodMenu")
    LiveData<List<FoodMenu>> loadAllFoods();

    @Query("SELECT * FROM foodMenu WHERE food_id = :food_id")
    LiveData<FoodMenu> idQuery(int food_id);

    @Query("SELECT * FROM foodMenu")
    List<FoodMenu> loadAllFoodsAdapter();

    @Insert
    void insertFood(FoodMenu foodMenu);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFood(FoodMenu foodMenu);

    @Delete
    void deleteFood(FoodMenu foodMenu);

    @Query("DELETE FROM foodMenu")
    void deleteAllFood();


    @Query("SELECT * FROM foodMenu WHERE id = :id")
    LiveData<FoodMenu> loadFoodById(int id);
}
