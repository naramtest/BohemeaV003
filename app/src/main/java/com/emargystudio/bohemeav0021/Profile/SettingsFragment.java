package com.emargystudio.bohemeav0021.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.HomeActivity;
import com.emargystudio.bohemeav0021.MainActivity;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    EditText phone_number;
    Button save;
    TextView logout;



    private SharedPreferenceManger sharedPreferenceManger;
    User user;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferenceManger = SharedPreferenceManger.getInstance(getContext());
        user = sharedPreferenceManger.getUserData();


        phone_number = view.findViewById(R.id.phone);
        save = view.findViewById(R.id.save);
        logout = view.findViewById(R.id.logout);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhone(Integer.parseInt(phone_number.getText().toString()));
                sharedPreferenceManger.updatPhoneNumber(Integer.parseInt(phone_number.getText().toString()));
                Log.d(TAG, "onClick: "+sharedPreferenceManger.getUserData().getUserPhoneNumber());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferenceManger.logUserOut();
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    public void updatePhone(final int phoneNumber){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.update_phone+user.getUserId()+"&user_phone_number="+phoneNumber,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){
                                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                                phone_number.setText("Done!");

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
                    }
                }
        );

        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);

    }


}
