package com.emargystudio.bohemeav0021.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emargystudio.bohemeav0021.InterFace.ItemClickListener;
import com.emargystudio.bohemeav0021.Model.FoodCategory;
import com.emargystudio.bohemeav0021.Model.FoodMenu;
import com.emargystudio.bohemeav0021.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodMenuAdapter extends RecyclerView.Adapter<FoodMenuAdapter.FoodMenuViewHolder>{

    private Context context;
    private ArrayList<FoodMenu> foodMenus;
    private ItemClickListener itemClickListener;

    public FoodMenuAdapter(Context context, ArrayList<FoodMenu> foodMenus, ItemClickListener itemClickListener) {
        this.context = context;
        this.foodMenus = foodMenus;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public FoodMenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.food_menu_item, viewGroup, false);
        final FoodMenuViewHolder holder = new FoodMenuViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(view,holder.getAdapterPosition(),false);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodMenuViewHolder holder, int i) {

        Picasso.get().load(foodMenus.get(i).getImage_url()).into(holder.foodImage);
        holder.foodName.setText(foodMenus.get(i).getName());

    }

    @Override
    public int getItemCount() {
        return foodMenus.size();
    }

    class FoodMenuViewHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView foodName;

        public FoodMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName  = itemView.findViewById(R.id.food_name);
        }

    }
}