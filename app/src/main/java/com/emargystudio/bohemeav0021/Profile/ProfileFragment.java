package com.emargystudio.bohemeav0021.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emargystudio.bohemeav0021.History.HistoryActivity;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private SharedPreferenceManger sharedPreferenceManger;
    User user;


    CircleImageView profile_photo;
    TextView userName , historyDetails;
    ImageView settings;
    Toolbar toolbar;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.your_placeholder, new SettingsFragment(),"Settings");
                ft.addToBackStack("Settings");
                ft.commit();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    }

    public void setupView(){
        user = sharedPreferenceManger.getUserData();
        Log.d(TAG, "setupView: "+user.getUserPhoto());
        Picasso.get().load(user.getUserPhoto()).into(profile_photo);
        userName.setText(user.getUserName());

    }

}
