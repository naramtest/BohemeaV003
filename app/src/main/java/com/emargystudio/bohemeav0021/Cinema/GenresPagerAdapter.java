package com.emargystudio.bohemeav0021.Cinema;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emargystudio.bohemeav0021.Model.Movie;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.CardAdapter;
import com.emargystudio.bohemeav0021.helperClasses.MovieItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GenresPagerAdapter extends PagerAdapter implements CardAdapter {


    private final MovieItemClickListener movieItemClickListener;

    private List<CardView> mViews;
    private List<Movie> mData;
    private float mBaseElevation;

    public GenresPagerAdapter(MovieItemClickListener movieItemClickListener) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        this.movieItemClickListener = movieItemClickListener;
    }

    public void addCardItem(Movie item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    public Movie getDate(int position){
        return mData.get(position);
    }



    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.genres_item, container, false);
        container.addView(view);
        final ImageView imageView = view.findViewById(R.id.new_movie_image);

        Picasso.get().load("https://image.tmdb.org/t/p/w500"+mData.get(position).getPoster_path()).into(imageView);

        CardView cardView = view.findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                movieItemClickListener.onMovieItemClick(position,mData.get(position),imageView);
            }
        });

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }


    @Override
    public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
        super.restoreState(state, loader);
    }
}
