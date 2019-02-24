package com.emargystudio.bohemeav0021.OrderDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.emargystudio.bohemeav0021.Model.FoodOrder;

import java.util.List;

@Dao
public interface OrderDao {

    @Query("SELECT * FROM food")
    LiveData<List<FoodOrder>> loadAllFoods();

    @Query("SELECT * FROM food")
    List<FoodOrder> loadAllFoodsAdapter();

    @Insert
    void insertFood(FoodOrder foodOrder);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFood(FoodOrder foodOrder);

    @Delete
    void deleteFood(FoodOrder foodOrder);

    @Query("DELETE FROM food")
    void deleteAllFood();


    @Query("SELECT * FROM food WHERE id = :id")
    LiveData<FoodOrder> loadFoodById(int id);
}
