package com.emargystudio.bohemeav0021.History;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Collections;


public class ResHistoryListFragment extends Fragment {

    private static final String TAG = "ResHistoryListFragment";

    User user;
    ArrayList<Reservation> reservations = new ArrayList<>();
    SharedPreferenceManger sharedPreferenceManger;
    ResHistoryAdapter resHistoryAdapter;

    public ResHistoryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_res_history_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferenceManger = SharedPreferenceManger.getInstance(getContext());
        user = sharedPreferenceManger.getUserData();
        reservationQuery();
        initRecyclerView(view);
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
                                            jsonObjectSingleRes.getInt("chairNumber"),
                                            jsonObjectSingleRes.getInt("status"),
                                            jsonObjectSingleRes.getInt("total")));


                                }
                                Collections.reverse(reservations);
                                resHistoryAdapter.notifyDataSetChanged();


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


    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.res_history);

        resHistoryAdapter = new ResHistoryAdapter(getContext(), reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(resHistoryAdapter);

    }

}
