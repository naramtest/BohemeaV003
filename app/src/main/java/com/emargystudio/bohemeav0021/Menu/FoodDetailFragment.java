package com.emargystudio.bohemeav0021.Menu;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.emargystudio.bohemeav0021.Model.FoodMenu;
import com.emargystudio.bohemeav0021.R;
import com.squareup.picasso.Picasso;


public class FoodDetailFragment extends Fragment {


    FoodMenu foodMenu;

    //widget
    TextView food_name , food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;


    public FoodDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        food_name = view.findViewById(R.id.food_name);
        food_description = view.findViewById(R.id.food_description);
        food_price       = view.findViewById(R.id.food_price);
        food_image = view.findViewById(R.id.image_food);
        btnCart = view.findViewById(R.id.btnCart);
        numberButton = view.findViewById(R.id.number_button);

        collapsingToolbarLayout = view.findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }



    private void getFoodDetail(FoodMenu foodMenu){

        food_name.setText(foodMenu.getName());
        food_description.setText(foodMenu.getDescription());
        food_price.setText(foodMenu.getPrice());
        Picasso.get().load(foodMenu.getImage_url()).into(food_image);
        
    }
}
