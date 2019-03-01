package com.emargystudio.bohemeav0021.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private SharedPreferenceManger sharedPreferenceManger;
    User user;


    CircleImageView profile_photo;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView userName , historyDetails;

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
                Intent intent = new Intent(getContext(),HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initView(View view){
        profile_photo = view.findViewById(R.id.profile_pic);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing);
        userName = view.findViewById(R.id.user_name);
        historyDetails = view.findViewById(R.id.history_details);

    }

    public void setupView(){
        user = sharedPreferenceManger.getUserData();
        Picasso.get().load(user.getUserPhoto()).into(profile_photo);
        userName.setText(user.getUserName());

    }

}
