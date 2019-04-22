package com.emargystudio.bohemeav0021.OrderDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.emargystudio.bohemeav0021.Model.Movie;


import java.util.List;


@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> loadAllFoods();

    @Query("SELECT * FROM movies WHERE tmdb_id = :tmdb_id")
    LiveData<Movie> idQuery(int tmdb_id);

    @Query("SELECT * FROM movies")
    List<Movie> loadAllFoodsAdapter();

    @Insert
    void insertFood(Movie foodOrder);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFood(Movie foodOrder);

    @Delete
    void deleteFood(Movie foodOrder);

    @Query("DELETE FROM movies")
    void deleteAllFood();


    @Query("SELECT * FROM movies WHERE id = :id")
    LiveData<Movie> loadFoodById(int id);
}
