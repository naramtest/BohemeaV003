package com.emargystudio.bohemeav0021.ViewHolder;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.emargystudio.bohemeav0021.InterFace.ItemClickListener;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;
import com.emargystudio.bohemeav0021.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter  extends RecyclerView.Adapter<CartViewHolder> {

    private List<FoodOrder> listData;
    private Context context;
    AppDatabase mdb;

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
    public void onBindViewHolder(@NonNull final CartViewHolder holder, int i) {

        //$ sign
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = listData.get(i).getPrice() * listData.get(i).getQuantity();
        holder.txt_price.setText(fmt.format(price));

        mdb = AppDatabase.getInstance(context);


        holder.txt_cart_name.setText(listData.get(i).getFood_name());
        holder.txt_quantity.setText(String.valueOf(listData.get(i).getQuantity()));
        Picasso.get().load(listData.get(i).getFood_image()).into(holder.foodImage);


        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(listData.get(holder.getAdapterPosition()));
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


    public void showDialog(final FoodOrder foodOrder){
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Make your order special");
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = li.inflate(R.layout.alert_update,null);
        final EditText add_note_Edt = alertLayout.findViewById(R.id.addNoteEdt);
        final TextView txt_price = alertLayout.findViewById(R.id.price);
        final ElegantNumberButton numberButton = alertLayout.findViewById(R.id.number_button);
        if (!foodOrder.getNote().isEmpty()){
            add_note_Edt.setText(foodOrder.getNote());
        }
        if (foodOrder.getPrice() != 0) {
            txt_price.setText("Price: "+String.valueOf(foodOrder.getPrice())+" S.P");
        }
        if (foodOrder.getQuantity() !=0 ){
            numberButton.setNumber(String.valueOf(foodOrder.getQuantity()));
        }

        alert.setView(alertLayout);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!add_note_Edt.getText().toString().isEmpty()){
                    foodOrder.setNote(add_note_Edt.getText().toString());
                }
                foodOrder.setQuantity(Integer.parseInt(numberButton.getNumber()));
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mdb.orderDao().updateFood(foodOrder);
                    }
                });

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }


}



