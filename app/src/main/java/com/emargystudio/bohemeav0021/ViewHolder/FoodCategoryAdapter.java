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
import com.emargystudio.bohemeav0021.R;

import java.util.ArrayList;

public class FoodCategoryAdapter  extends RecyclerView.Adapter<FoodCategoryAdapter.FoodCategoryViewHolder>{

    private Context context;
    private ArrayList<FoodCategory> foodCategories;
    private int currentCategory;
    private ItemClickListener itemClickListener;

    public FoodCategoryAdapter(Context context, ArrayList<FoodCategory> foodCategories, int currentCategory, ItemClickListener itemClickListener) {
        this.context = context;
        this.foodCategories = foodCategories;
        this.currentCategory = currentCategory;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public FoodCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_list_item, viewGroup, false);
        final FoodCategoryViewHolder holder = new FoodCategoryViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(view,holder.getAdapterPosition(),false);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodCategoryViewHolder holder, int i) {

        holder.category_text.setText(foodCategories.get(i).getName());
        if (i == currentCategory){
            holder.doneIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return foodCategories.size();
    }

    class FoodCategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView doneIcon;
        TextView category_text;

        public FoodCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            doneIcon = itemView.findViewById(R.id.done);
            category_text  = itemView.findViewById(R.id.categoryItem);
        }

    }
}