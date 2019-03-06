package com.emargystudio.bohemeav0021.History;


import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ViewHolder.OrderHistoryAdapter;
import com.emargystudio.bohemeav0021.helperClasses.CommonReservation;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OrderHistoryFragment extends Fragment implements OrderHistoryAdapter.EventHandler {

    private static final String TAG = "OrderHistoryFragment";

    OrderHistoryAdapter orderHistoryAdapter;
    ArrayList<FoodOrder> foodOrders = new ArrayList<>();
    ArrayList<FoodOrder> newFoodOrders = new ArrayList<>();
    Reservation reservation;
    User user;
    SharedPreferenceManger sharedPreferenceManger;


    //widgets
    TextView date,hour,table_number ,reservation_for,name, totalTxt, back;
    RecyclerView recyclerView;
    View divider;
    TextView noOrder, summaryTxt;

    ProgressBar progressBar;
    Button  submit,cancel;


    public OrderHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferenceManger = SharedPreferenceManger.getInstance(getContext());
        user = sharedPreferenceManger.getUserData();
        reservation = getReservationFromBundle();

        initViews(view);

       if (reservation!=null){
           if (reservation.getTotal()==0){
               recyclerView.setVisibility(View.GONE);
           }else {
               noOrder.setVisibility(View.GONE);
               initRecyclerView();
               orderQuery();
           }
       }



        setUpView();
       cancelReservation();
       submitOrder();
    }

    //init methods
    private void initViews(@NonNull View view) {
        date = view.findViewById(R.id.date);
        hour = view.findViewById(R.id.hour);
        table_number = view.findViewById(R.id.table_number);
        reservation_for = view.findViewById(R.id.reservation_for);
        name = view.findViewById(R.id.name);
        totalTxt = view.findViewById(R.id.total);
        back = view.findViewById(R.id.back);
        recyclerView = view.findViewById(R.id.order_history);
        divider = view.findViewById(R.id.divider);
        progressBar = view.findViewById(R.id.progress_bar);
        noOrder = view.findViewById(R.id.noOrder);
        submit = view.findViewById(R.id.submit);
        cancel = view.findViewById(R.id.cancel);
        summaryTxt = view.findViewById(R.id.summaryTxt);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NABILA.TTF");
        summaryTxt.setTypeface(face);

    }
    public void orderQuery(){
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.order_query_id+reservation.getRes_id(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d(TAG, "onResponse: "+response);

                            if(!jsonObject.getBoolean("error")){

                                JSONArray orderArrayJson =  jsonObject.getJSONArray("orders");

                                for(int i = 0 ; i<orderArrayJson.length(); i++){
                                    JSONObject orderJson = orderArrayJson.getJSONObject(i);
                                    foodOrders.add(new FoodOrder(orderJson.getInt("id"),
                                            orderJson.getInt("res_id"),
                                            orderJson.getInt("food_id"),
                                            orderJson.getString("food_name"),
                                            orderJson.getInt("quantity"),
                                            orderJson.getInt("price"),
                                            orderJson.getInt("discount"),
                                            " ",
                                            orderJson.getString("note")));

                                }

                                progressBar.setVisibility(View.GONE);
                                orderHistoryAdapter.notifyDataSetChanged();

                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Please check your internet connection and try again later .... ", Toast.LENGTH_SHORT).show();

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"response error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: "+ error.getMessage());
                    }
                }

        );


        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        orderHistoryAdapter = new OrderHistoryAdapter(getContext(), foodOrders,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(orderHistoryAdapter);
    }
    private Reservation getReservationFromBundle(){

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.reservation_bundle));
        }else{
            return null;
        }
    }
    public void setUpView(){
        if (reservation!=null){
            //date
            int year = reservation.getYear();
            int month = reservation.getMonth();
            int day = reservation.getDay();
            String dateString = "Date: "+year+"/"+month+"/"+day;
            date.setText(dateString);

            //hour
            double dHour = reservation.getStartHour();
            String sHour = CommonReservation.changeHourFormat(dHour);
            hour.setText(sHour);

            //table and chair number
            table_number.setText(String.valueOf("Table Number: "+reservation.getTable_id()));
            reservation_for.setText(String.valueOf("For: "+reservation.getChairNumber()));

            //totalTxt
            totalTxt.setText(String.valueOf(reservation.getTotal()+" S.P"));

            //name and phone number
            if (user!=null){
                name.setText(user.getUserName());
            }

        }

    }


    //adapter listener
    @Override
    public void handle(int position) {
        submit.setVisibility(View.VISIBLE);
    }

    //cancel reservation
    public void cancelReservation(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlert();
            }
        });
    }
    public void cancelAlert(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Are you sure you want to cancel this reservation");
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("Yse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.cancelReservation+reservation.getRes_id()+"&status="+2,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Log.d(TAG, "onResponse: "+response);

                                    if(!jsonObject.getBoolean("error")){
                                        Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                                        getActivity().onBackPressed();
                                    }else{
                                        Toast.makeText(getContext(), "Please check your internet connection and try again later .... ", Toast.LENGTH_SHORT).show();

                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(),"response error", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "onErrorResponse: "+ error.getMessage());
                            }
                        }
                );

                VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }


    //edit order
    public void submitOrder(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrder();
            }
        });
    }
    public void updateOrder(){
        Gson gson=new Gson();
        newFoodOrders = orderHistoryAdapter.getFoodOrders();
        final int total = getTotal(newFoodOrders);
        final String newDataArray=gson.toJson(newFoodOrders);
        Log.d(TAG, "updateOrder: "+newDataArray);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.edit_order,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();
                        totalTxt.setText(String.valueOf(total+" S.P"));


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        error.getMessage();
                    }
                }
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("array",newDataArray);
                param.put("totalTxt", String.valueOf(total));
                param.put("res_id",String.valueOf(reservation.getRes_id()));// array is key which we will use on server side

                return param;
            }
        };//end of string Request

        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }

    public int getTotal(ArrayList<FoodOrder> foodOrders){
        int total = 0;
        for (int i = 0 ; i<foodOrders.size();i++){
            total+= foodOrders.get(i).getPrice()*foodOrders.get(i).getQuantity();
        }
        return total;
    }




    //fragment restore in landscape mod
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (reservation!=null) {
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.reservation_bundle), reservation);
            outState.putBundle("args", args);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.d(TAG, "onViewCreated: " + savedInstanceState.toString());
            Bundle bundle = savedInstanceState.getBundle("args");
            if (bundle != null) {
                reservation = bundle.getParcelable(getString(R.string.reservation_bundle));
                setUpView();
            }
        }
        super.onActivityCreated(savedInstanceState);
    }
}
