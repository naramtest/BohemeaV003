package com.emargystudio.bohemeav0021.ReservationMaker;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.emargystudio.bohemeav0021.HomeActivity;
import com.emargystudio.bohemeav0021.R;

public class ReservationActivity extends AppCompatActivity {

    private static final String TAG = "ReservationActivity";



    ImageView next;
    Fragment datafragment;
    Fragment fragment;
    Fragment tableFragment;


    //var

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
            ft.replace(R.id.your_placeholder, new DataFragment(),"Data");
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
