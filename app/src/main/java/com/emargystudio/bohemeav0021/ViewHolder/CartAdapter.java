package com.emargystudio.bohemeav0021.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.emargystudio.bohemeav0021.InterFace.ItemClickListener;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter  extends RecyclerView.Adapter<CartViewHolder> {

    private List<FoodOrder> listData;
    private Context context;

    public CartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_item, viewGroup, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int i) {

        //$ sign
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = listData.get(i).getPrice() * listData.get(i).getQuantity();
        holder.txt_price.setText(fmt.format(price));


        holder.txt_cart_name.setText(listData.get(i).getFood_name());
        holder.txt_quantity.setText(String.valueOf(listData.get(i).getQuantity()));
        Picasso.get().load(listData.get(i).getFood_image()).into(holder.foodImage);


        holder.txt_cart_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Naram", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        if (listData == null) {
            return 0;
        }
        return listData.size();
    }

    public List<FoodOrder> getTasks() {
        return listData;
    }

    public void setTasks(List<FoodOrder> taskEntries) {
        listData = taskEntries;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        listData.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(FoodOrder item, int position) {
        listData.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


}



