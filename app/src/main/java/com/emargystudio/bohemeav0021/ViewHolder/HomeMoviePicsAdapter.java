package com.emargystudio.bohemeav0021.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emargystudio.bohemeav0021.Model.Movie;

import com.emargystudio.bohemeav0021.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeMoviePicsAdapter extends RecyclerView.Adapter<HomeMoviePicsAdapter.NewMovieViewHolder> {

    private List<Movie> imagesUrl;

    public void setImagesUrl(List<Movie> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public HomeMoviePicsAdapter(List<Movie> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    @NonNull
    @Override
    public NewMovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_pics,viewGroup,false);
        return new NewMovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewMovieViewHolder holder, int i) {

        Picasso.get().load("https://image.tmdb.org/t/p/w500"+imagesUrl.get(i).getPoster_path()).into(holder.moviePhoto);
        holder.cardView.setElevation(6f);


    }

    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }

    class NewMovieViewHolder extends RecyclerView.ViewHolder {
        ImageView moviePhoto;
        CardView cardView;

        NewMovieViewHolder(View view) {
            super(view);
            moviePhoto =  view.findViewById(R.id.movie_home_pics);
            cardView = view.findViewById(R.id.cardView);

        }
    }
}
