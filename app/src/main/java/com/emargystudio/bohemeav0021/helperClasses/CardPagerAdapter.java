package com.emargystudio.bohemeav0021.helperClasses;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emargystudio.bohemeav0021.Model.Movie;
import com.emargystudio.bohemeav0021.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {



    private final MovieItemClickListener movieItemClickListener;

    private List<CardView> mViews;
    private List<Movie> mData;
    private float mBaseElevation;

    public CardPagerAdapter(MovieItemClickListener movieItemClickListener) {
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

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.card_movie_flow, container, false);
        container.addView(view);
        final ImageView imageView = view.findViewById(R.id.new_movie_image);
        TextView movieTitle = view.findViewById(R.id.new_movie_title);

        Picasso.get().load("https://image.tmdb.org/t/p/w500"+mData.get(position).getBackdrop_path()).into(imageView);
        ViewCompat.setTransitionName(imageView,mData.get(position).getTitle());
        movieTitle.setText(mData.get(position).getTitle());

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
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }



}
