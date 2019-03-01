package com.emargystudio.bohemeav0021.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public void onBindViewHolder(@NonNull RisHistoryViewHolder holder, int i) {

        int year = reservations.get(i).getYear();
        int month = reservations.get(i).getMonth();
        int day = reservations.get(i).getDay();

        String date = year+"/"+month+"/"+day;


        holder.res_date.setText(date);
        holder.res_id.setText("#"+String.valueOf(reservations.get(i).getRes_id()));
        holder.price.setText(String.valueOf(reservations.get(i).getStartHour() + " S.P"));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "pressed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return reservations.size();
    }

    class RisHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView res_date, price , res_id ;
        ImageView  order;
        CardView cardView;
        private ItemClickListener itemClickListener;

        public RisHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            res_date  = itemView.findViewById(R.id.res_date);
            price = itemView.findViewById(R.id.price);
            res_id = itemView.findViewById(R.id.res_id);
            order = itemView.findViewById(R.id.order);
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
