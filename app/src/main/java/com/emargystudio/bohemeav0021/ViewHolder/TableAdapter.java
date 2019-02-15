package com.emargystudio.bohemeav0021.ViewHolder;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emargystudio.bohemeav0021.Common.CommonReservation;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ReservationMaker.ReservationActivity;
import com.emargystudio.bohemeav0021.ReservationMaker.ReservationSummaryFragment;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {

    private static final String TAG = "TableAdapter";

    private ArrayList<String> mNames ;
    private ArrayList<String> mImages ;
    private Context context;
    private Reservation reservation;
    private User user;



    public TableAdapter(Context context ,ArrayList<String> mNames, ArrayList<String> mImages , Reservation reservation) {
        this.mNames = mNames;
        this.mImages = mImages;
        this.context = context;
        this.reservation= reservation;
        user = SharedPreferenceManger.getInstance(context).getUserData();
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.table_item, viewGroup, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TableViewHolder holder, final int position) {

        RequestOptions requestOptions = new RequestOptions().placeholder(R.mipmap.ic_launcher);
        Glide.with(context)
                .load(mImages.get(position))
                .apply(requestOptions)
                .into(holder.table_image);

        holder.table_text.setText(mNames.get(position));
        holder.table_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               alertTable(mImages.get(position),mNames.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public void alertTable(String imageUrl, final String name ){
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = li.inflate(R.layout.alert_table, null);
        ImageView table_item_image = alertLayout.findViewById(R.id.table_item_image);
        Button chooseBtn = alertLayout.findViewById(R.id.choose);
        Button backbtn = alertLayout.findViewById(R.id.back);
        final ProgressBar progressBar = alertLayout.findViewById(R.id.progress);

        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        Typeface face = Typeface.createFromAsset(context.getAssets(),"fonts/NABILA.TTF");
        RequestOptions requestOptions = new RequestOptions().placeholder(R.mipmap.ic_launcher);
        Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(table_item_image);

        final AlertDialog dialog = alert.create();
        dialog.show();
        chooseBtn.setTypeface(face);
        backbtn.setTypeface(face);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reservation.setTable_id(Integer.parseInt(name));
                sendReservation(dialog,user.getUserId(),reservation,progressBar);



            }
        });


    }

    private void sendReservation(final AlertDialog dialog,final int user_id, final Reservation reservation,final ProgressBar progressBar){

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.send_reservation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){


                                //go to summary fragment and add reservation as a bundle to setup TextView int summaryFragment
                                Fragment fragment = new ReservationSummaryFragment();
                                Bundle args = new Bundle();
                                args.putParcelable(context.getString(R.string.reservation_bundle), reservation);
                                fragment.setArguments(args);
                                FragmentTransaction ft = ((ReservationActivity)context).getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.your_placeholder, fragment,"Summary");
                                ft.commit();
                                progressBar.setVisibility(View.GONE);
                                dialog.dismiss();
                                Toast.makeText(context, "Done...!!!", Toast.LENGTH_SHORT).show();


                            }else {
                                Toast.makeText(context, context.getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: "+error.getMessage());
                    }
                }
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map ReservationData = new HashMap<>();
                ReservationData.put("user_id",String.valueOf(user_id));
                ReservationData.put("table_id",String.valueOf(reservation.getTable_id()));
                ReservationData.put("year",String.valueOf(reservation.getYear()));
                ReservationData.put("month",String.valueOf(reservation.getMonth()));
                ReservationData.put("day",String.valueOf(reservation.getDay()));
                ReservationData.put("hours",String.valueOf(reservation.getStartHour()));
                ReservationData.put("end_hour",String.valueOf(reservation.getEnd_hour()));
                ReservationData.put("chairNumber",String.valueOf(reservation.getChairNumber()));

                return  ReservationData;
            }
        };//end of string Request

        VolleyHandler.getInstance(context).addRequetToQueue(stringRequest);





    }
}
