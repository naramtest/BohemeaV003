package com.emargystudio.bohemeav0021.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ViewHolder.ResHistoryAdapter;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";
    User user;
    ArrayList<Reservation> reservations = new ArrayList<>();
    SharedPreferenceManger sharedPreferenceManger;
    ResHistoryAdapter resHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedPreferenceManger = SharedPreferenceManger.getInstance(HistoryActivity.this);
        user = sharedPreferenceManger.getUserData();
        reservationQuery();
        initRecyclerView();
    }


    public void reservationQuery(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.reservation_query_id+user.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                JSONArray jsonArrayReservation =  jsonObject.getJSONArray("reservations");

                                for(int i = 0 ; i<jsonArrayReservation.length(); i++){
                                    JSONObject jsonObjectSingleRes = jsonArrayReservation.getJSONObject(i);
                                    Log.d(TAG, "onResponse: "+jsonObjectSingleRes.toString());
                                    reservations.add(new Reservation(jsonObjectSingleRes.getInt("res_id"),
                                            jsonObjectSingleRes.getInt("user_id"),
                                            jsonObjectSingleRes.getInt("table_id"),
                                            jsonObjectSingleRes.getInt("year"),
                                            jsonObjectSingleRes.getInt("month"),
                                            jsonObjectSingleRes.getInt("day"),
                                            jsonObjectSingleRes.getDouble("hours"),
                                            jsonObjectSingleRes.getDouble("end_hour"),
                                            jsonObjectSingleRes.getInt("chairNumber")));


                                }
                                Collections.reverse(reservations);
                                resHistoryAdapter.notifyDataSetChanged();


                            }else{
                                Toast.makeText(HistoryActivity.this, "Please check your internet connection and try again later .... ", Toast.LENGTH_SHORT).show();

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HistoryActivity.this,"response error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: "+ error.getMessage());
                    }
                }

        );


        VolleyHandler.getInstance(HistoryActivity.this).addRequetToQueue(stringRequest);
    }


    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.res_history);

        resHistoryAdapter = new ResHistoryAdapter(HistoryActivity.this, reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resHistoryAdapter);

    }
}
