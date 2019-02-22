package com.emargystudio.bohemeav0021.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.emargystudio.bohemeav0021.InterFace.ItemClickListener;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.R;

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
        TextDrawable drawable = TextDrawable.builder().buildRound("" + listData.get(i).getQuantity(), Color.BLUE);
        holder.img_cart_count.setImageDrawable(drawable);
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = listData.get(i).getPrice() * listData.get(i).getQuantity();
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(i).getFood_name());


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

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<FoodOrder> taskEntries) {
        listData = taskEntries;
        notifyDataSetChanged();
    }


}



    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txt_cart_name,txt_price;
        public ImageView img_cart_count;

        private ItemClickListener itemClickListener;

        public void setTxt_cart_name(TextView txt_cart_name) {
            this.txt_cart_name = txt_cart_name;
        }

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_cart_name = itemView.findViewById(R.id.cart_item_name);
            txt_price = itemView.findViewById(R.id.cart_item_price);
            img_cart_count = itemView.findViewById(R.id.cart_item_count);

        }

        @Override
        public void onClick(View v) {

        }
    }

