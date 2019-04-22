package com.emargystudio.bohemeav0021.helperClasses;

import android.widget.ImageView;

import com.emargystudio.bohemeav0021.Model.Movie;


public interface MovieItemClickListener {

    void onMovieItemClick(int pos, Movie movie, ImageView shareImageView);
}
