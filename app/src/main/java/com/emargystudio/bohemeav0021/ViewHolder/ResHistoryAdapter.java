package com.emargystudio.bohemeav0021.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emargystudio.bohemeav0021.History.HistoryActivity;
import com.emargystudio.bohemeav0021.History.OrderHistoryFragment;
import com.emargystudio.bohemeav0021.History.ResHistoryListFragment;
import com.emargystudio.bohemeav0021.InterFace.ItemClickListener;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.R;

import java.util.ArrayList;

public class ResHistoryAdapter extends RecyclerView.Adapter<ResHistoryAdapter.RisHistoryViewHolder> {

    private Context context;
    private ArrayList<Reservation> reservations;
    private ItemClickListener itemClickListener;

    public ResHistoryAdapter(Context context, ArrayList<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations;

    }

    @NonNull
    @Override
    public RisHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.res_history_item, viewGroup, false);
        return new RisHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RisHistoryViewHolder holder, int i) {

        int year = reservations.get(i).getYear();
        int month = reservations.get(i).getMonth();
        int day = reservations.get(i).getDay();

        String date = year+"/"+month+"/"+day;
        int status = reservations.get(i).getStatus();
        switch (status){
            case 0:
                holder.status.setText(context.getString(R.string.waiting));
                holder.status.setTextColor(Color.parseColor("#dd3538"));
                break;

            case 1:
                holder.status.setText(context.getString(R.string.aprroved));
                holder.status.setTextColor(Color.parseColor("#006400"));
                break;
        }


        holder.res_date.setText(date);
        //holder.res_id.setText("#"+String.valueOf(reservations.get(i).getRes_id()));
        holder.price.setText("Total: "+String.valueOf(reservations.get(i).getTotal() + " S.P"));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putParcelable("reservation", reservations.get(holder.getAdapterPosition()));
                Fragment fragment = new OrderHistoryFragment();
                fragment.setArguments(args);
                FragmentTransaction ft = ((HistoryActivity)context).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.your_placeholder, fragment,"Order");
                ft.addToBackStack("Order");
                ft.commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return reservations.size();
    }

    class RisHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView res_date, price , res_id,status ;
        CardView cardView;
        private ItemClickListener itemClickListener;

        public RisHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            res_date  = itemView.findViewById(R.id.res_date);
            price = itemView.findViewById(R.id.price);
            //res_id = itemView.findViewById(R.id.res_id);
            status = itemView.findViewById(R.id.status);
            cardView = itemView.findViewById(R.id.res_container);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
