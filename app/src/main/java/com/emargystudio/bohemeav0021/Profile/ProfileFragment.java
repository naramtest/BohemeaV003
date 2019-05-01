package com.emargystudio.bohemeav0021.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.History.HistoryActivity;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    private SharedPreferenceManger sharedPreferenceManger;
    User user;


    CircleImageView profile_photo;
    TextView userName , historyDetails ,reservation_counter;
    ImageView settings;
    Toolbar toolbar;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferenceManger = SharedPreferenceManger.getInstance(getContext());
        initView(view);
        setupView();



        //listener
        historyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity()!=null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.your_placeholder, new SettingsFragment(), "Settings");
                    ft.addToBackStack("Settings");
                    ft.commit();
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity()!=null)
                getActivity().onBackPressed();
            }
        });


    }

    public void initView(View view){
        profile_photo = view.findViewById(R.id.profile_pic);
        userName = view.findViewById(R.id.user_name);
        historyDetails = view.findViewById(R.id.history_details);
        settings = view.findViewById(R.id.settings);
        toolbar = view.findViewById(R.id.tool_bar);
        reservation_counter = view.findViewById(R.id.reservation_counter);


    }

    public void setupView(){
        user = sharedPreferenceManger.getUserData();
        Picasso.get().load(user.getUserPhoto()).into(profile_photo);
        userName.setText(user.getUserName());
        reservationQuery();
    }


    public void reservationQuery(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.reservation_query_id+user.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int reservation_co = 0;
                            if(!jsonObject.getBoolean("error")){

                                JSONArray jsonArrayReservation =  jsonObject.getJSONArray("reservations");


                                for(int i = 0 ; i<jsonArrayReservation.length(); i++){
                                    JSONObject jsonObjectSingleRes = jsonArrayReservation.getJSONObject(i);
                                    if (jsonObjectSingleRes.getInt("status") !=2) {
                                        reservation_co += 1;
                                    }
                                }
                                reservation_counter.setText(String.valueOf(reservation_co));

                            }else{

                                if (getActivity()!=null)
                                    Toast.makeText(getContext(), getActivity().getString(R.string.internet_off), Toast.LENGTH_SHORT).show();

                            }
                        }catch (JSONException e){

                            if (getActivity()!=null)
                                Toast.makeText(getContext(), getActivity().getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (getActivity()!=null)
                            Toast.makeText(getContext(), getActivity().getString(R.string.internet_off), Toast.LENGTH_SHORT).show();

                    }
                }

        );


        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }

}
