package com.emargystudio.bohemeav0021.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emargystudio.bohemeav0021.InterFace.ItemClickListener;
import com.emargystudio.bohemeav0021.R;

public class TableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView table_image;
    TextView table_text;
    private ItemClickListener itemClickListener;
    ProgressBar progressBar;

    public TableViewHolder(@NonNull View itemView) {
        super(itemView);
        table_image = itemView.findViewById(R.id.table_image);
        table_text  = itemView.findViewById(R.id.table_number);
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
