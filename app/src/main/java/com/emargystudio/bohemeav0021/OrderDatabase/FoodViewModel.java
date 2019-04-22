package com.emargystudio.bohemeav0021.OrderDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.emargystudio.bohemeav0021.Model.FoodMenu;
import com.emargystudio.bohemeav0021.Model.Movie;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FoodViewModel extends AndroidViewModel {



    private LiveData<List<FoodMenu>> foodMenus;
    private LiveData<FoodMenu> foodMenu;
    AppDatabase database;

    public FoodViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(this.getApplication());
        foodMenus = database.foodDao().loadAllFoods();

    }

    public LiveData<List<FoodMenu>> getTasks() {
        return foodMenus;
    }

    public LiveData<FoodMenu> getFood(int id) {
        foodMenu = database.foodDao().idQuery(id);
        return foodMenu;
    }
}
