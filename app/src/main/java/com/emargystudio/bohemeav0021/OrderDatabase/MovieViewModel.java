package com.emargystudio.bohemeav0021.OrderDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.emargystudio.bohemeav0021.Model.Movie;


import java.util.List;

public class MovieViewModel extends AndroidViewModel {



    private LiveData<List<Movie>> movies;
    private LiveData<Movie> movie;
    AppDatabase database;

    public MovieViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(this.getApplication());
        movies = database.movieDao().loadAllFoods();

    }

    public LiveData<List<Movie>> getTasks() {
        return movies;
    }

    public LiveData<Movie> getMovie(int id) {
        movie = database.movieDao().idQuery(id);
        return movie;
    }
}
