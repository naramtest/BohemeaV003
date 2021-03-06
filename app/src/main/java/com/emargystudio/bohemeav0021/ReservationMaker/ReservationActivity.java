package com.emargystudio.bohemeav0021.ReservationMaker;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.emargystudio.bohemeav0021.R;

public class ReservationActivity extends AppCompatActivity {


    Fragment datafragment;
    Fragment fragment;
    Fragment tableFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        if (savedInstanceState != null) {
            //Restore the dataFragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.your_placeholder, fragment);
            ft.commit();
        }else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment dataFragment = new DataFragment();
            if (getIntent()!=null){
                String movie_name = getIntent().getStringExtra("movie_name");
                Bundle bundle = new Bundle();
                bundle.putString("movie_name", movie_name);
                dataFragment.setArguments(bundle);
            }
            ft.replace(R.id.your_placeholder,dataFragment,"Data");
            ft.commit();
        }

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the dataFragment's instance

        datafragment = getSupportFragmentManager().findFragmentByTag("Data");
        tableFragment = getSupportFragmentManager().findFragmentByTag("table");

        if (datafragment != null && datafragment.isVisible()) {
            getSupportFragmentManager().putFragment(outState, "myFragmentName", datafragment);
        }else if (tableFragment != null && tableFragment.isVisible()){
            getSupportFragmentManager().putFragment(outState, "myFragmentName", tableFragment);
        }
    }

}
