package com.emargystudio.bohemeav0021.ReservationMaker;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Common;
import com.emargystudio.bohemeav0021.HomeActivity;
import com.emargystudio.bohemeav0021.Menu.MenuActivity;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.Model.Reservation;

import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;

import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emargystudio.bohemeav0021.Common.isOrdered;
import static com.emargystudio.bohemeav0021.Common.res_id;
import static com.emargystudio.bohemeav0021.Common.total;


public class TableFragment extends Fragment {

    private static final String TAG = "TableFragment";

    //widget
    TextView textView;
    TabLayout tabLayout;
    ImageView table_image;


    //var

    private Reservation reservation;
    private ArrayList<Integer> tableArray;
    private User user;
    private AppDatabase mDb;
    private List<FoodOrder> foodList;
    private String movie_name;





    public TableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        textView = view.findViewById(R.id.textView);
        tabLayout = view.findViewById(R.id.tableLayout);
        table_image = view.findViewById(R.id.table_image);
        tableArray = new ArrayList<>();
        foodList = new ArrayList<>();
        mDb = AppDatabase.getInstance(getContext());



        try {
            reservation = getReservationFromBundle();
            user = SharedPreferenceManger.getInstance(getContext()).getUserData();
            movie_name = reservation.getMovie_name();
            if (movie_name!=null){
                cinemaReservation();
            }
            reservationQuery();

        }catch (NullPointerException e){
            Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
        }

        loadListFood();
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NABILA.TTF");
        textView.setTypeface(face);


    }

    private void cinemaReservation() {
        tabLayout.setVisibility(View.GONE);
        table_image.setVisibility(View.GONE);
        reservationQuery();
        sendReservation(user.getUserId(),reservation,"-1",movie_name);

    }


    public void reservationQuery(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.reservation_query+ reservation.getYear()+"&month="+ reservation.getMonth()+"&day="+ reservation.getDay(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                JSONArray jsonArrayReservation =  jsonObject.getJSONArray("reservations");

                                Log.i("arrayReservation",jsonArrayReservation.toString());

                                for(int i = 0 ; i<jsonArrayReservation.length(); i++){
                                    JSONObject jsonObjectSingleRes = jsonArrayReservation.getJSONObject(i);
                                    Log.i("jsonObjectSingleRes",jsonObjectSingleRes.toString());

                                    double startHour = jsonObjectSingleRes.getDouble("hours");
                                    double endHour   = jsonObjectSingleRes.getDouble("end_hour");
                                    if (reservation.getStartHour() >= startHour && reservation.getStartHour() <= endHour){
                                        tableArray.add(jsonObjectSingleRes.getInt("table_id"));
                                        Log.d(TAG, "onResponse: "+jsonObjectSingleRes.getInt("table_id"));
                                    }
                                }
                                if (movie_name == null){
                                    initTabLayout();
                                }
                            }else{
                                Toast.makeText(getContext(), "Please check your internet connection and try again later .... ", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onResponse: " + jsonObject.getString("message"));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"response error",Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: "+ error.getMessage());
                    }
                }

        );


        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }

    private void initTabLayout() {

        for (int i = 0 ; i < 10 ; i++){
            if (!tableArray.contains(i+1)){
                tabLayout.addTab(tabLayout.newTab().setText(String.valueOf(i+1)));
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                alertSend(user.getUserId(),reservation,tab.getText().toString(),"No movie");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private Reservation getReservationFromBundle(){

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.reservation_bundle));
        }else{
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    private void loadListFood() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                foodList = mDb.orderDao().loadAllFoodsAdapter();
                if (!foodList.isEmpty()){
                    Common.isOrdered = true;
                }
            }
        });
    }

    private void sendReservation(final int user_id, final Reservation reservation, final String table_id, final String movie_name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.send_reservation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){

                                JSONObject jsonObjectUser =  jsonObject.getJSONObject("reservation");
                                Common.res_id = jsonObjectUser.getInt("res_id");
                                Log.d(TAG, "onResponse: "+Common.res_id);

                                if (Common.isOrdered){
                                    sendOrder();
                                }
                                alertDone();

                            }else {
                                Toast.makeText(getContext(), getContext().getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
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
                ReservationData.put("table_id",table_id);
                ReservationData.put("year",String.valueOf(reservation.getYear()));
                ReservationData.put("month",String.valueOf(reservation.getMonth()));
                ReservationData.put("day",String.valueOf(reservation.getDay()));
                ReservationData.put("hours",String.valueOf(reservation.getStartHour()));
                ReservationData.put("end_hour",String.valueOf(reservation.getEnd_hour()));
                ReservationData.put("chairNumber",String.valueOf(reservation.getChairNumber()));
                ReservationData.put("movie_name",movie_name);

                return  ReservationData;
            }
        };
        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }

    private void sendOrder() {
        Gson gson=new Gson();
        final String newDataArray=gson.toJson(foodList);
        Log.d(TAG, "sendOrder: "+foodList);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.test,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Common.clearCommon();
                        Common.isSended = true;
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mDb.orderDao().deleteAllFood();
                            }
                        });
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
                param.put("total", String.valueOf(total));
                param.put("res_id",String.valueOf(res_id));
                return param;
            }
        };
        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }

    public void alertDone(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = li.inflate(R.layout.alert_reser_done,null);
        LinearLayout menu = alertLayout.findViewById(R.id.menu_container);
        RelativeLayout menuTxt = alertLayout.findViewById(R.id.menu);
        Log.d(TAG, "alertDone: "+isOrdered);
        if (isOrdered){
            menuTxt.setVisibility(View.GONE);
        }
        alert.setView(alertLayout);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void alertSend(final int user_id, final Reservation reservation, final String table_id, final String movie_name){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Table Number "+table_id);
        alert.setMessage("Are you sure that you want to choose this table");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendReservation(user_id,reservation,table_id,movie_name);
            }
        });
        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }



}
