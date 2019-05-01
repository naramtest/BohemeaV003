package com.emargystudio.bohemeav0021.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.emargystudio.bohemeav0021.MainActivity;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.facebook.login.LoginManager;


import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {



    EditText phone_number;
    Button save;
    TextView logout;



    private SharedPreferenceManger sharedPreferenceManger;
    User user;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferenceManger.logUserOut();
                LoginManager.getInstance().logOut();
                Toast.makeText(getContext(), getString(R.string.setting_logout_toast), Toast.LENGTH_SHORT).show();
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
                                if (getActivity()!=null)
                                phone_number.setText(getActivity().getString(R.string.order_his_cancel_dialog_done));

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
