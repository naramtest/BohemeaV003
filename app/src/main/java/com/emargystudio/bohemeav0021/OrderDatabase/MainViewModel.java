package com.emargystudio.bohemeav0021.OrderDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.emargystudio.bohemeav0021.Model.FoodOrder;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<FoodOrder>> order;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        order = database.orderDao().loadAllFoods();
    }

    public LiveData<List<FoodOrder>> getTasks() {
        return order;
    }
}
