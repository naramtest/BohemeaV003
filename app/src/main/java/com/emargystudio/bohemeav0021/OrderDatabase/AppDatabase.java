package com.emargystudio.bohemeav0021.OrderDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.emargystudio.bohemeav0021.Model.FoodMenu;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.Model.Movie;

@Database(entities = {FoodOrder.class, Movie.class, FoodMenu.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "bohemea_database";
    private static AppDatabase sInstance;


    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract OrderDao orderDao();

    public abstract MovieDao movieDao();
    public abstract FoodDao foodDao();

}
