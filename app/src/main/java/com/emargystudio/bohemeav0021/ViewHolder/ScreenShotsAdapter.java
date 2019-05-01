package com.emargystudio.bohemeav0021.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.emargystudio.bohemeav0021.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ScreenShotsAdapter extends RecyclerView.Adapter<ScreenShotsAdapter.ScreenShotsViewHolder> {

    private List<String> imagesUrl;

    public ScreenShotsAdapter(List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    @NonNull
    @Override
    public ScreenShotsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_pics,viewGroup,false);
        return new ScreenShotsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenShotsViewHolder holder, int i) {

        Picasso.get().load("https://image.tmdb.org/t/p/w500"+imagesUrl.get(i)).into(holder.moviePhoto);

    }

    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }

    class ScreenShotsViewHolder extends RecyclerView.ViewHolder {
        ImageView moviePhoto;

        ScreenShotsViewHolder(View view) {
            super(view);
            moviePhoto = view.findViewById(R.id.movie_home_pics);
        }
    }
}
