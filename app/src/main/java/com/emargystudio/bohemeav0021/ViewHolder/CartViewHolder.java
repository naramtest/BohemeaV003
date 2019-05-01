package com.emargystudio.bohemeav0021.ViewHolder;

import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.emargystudio.bohemeav0021.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_cart_name,txt_price,txt_quantity;
    public RelativeLayout viewBackground ,viewForeground;
    public ImageView foodImage;
    public FrameLayout frameLayout;



    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.name);
        txt_price = itemView.findViewById(R.id.price);
        txt_quantity= itemView.findViewById(R.id.quantity);
        viewBackground = itemView.findViewById(R.id.view_background);
        viewForeground = itemView.findViewById(R.id.view_foreground);
        foodImage = itemView.findViewById(R.id.thumbnail);
        frameLayout = itemView.findViewById(R.id.cart_item_frameLayout);

    }

    @Override
    public void onClick(View v) {

    }
}
