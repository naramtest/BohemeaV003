package com.emargystudio.bohemeav0021.History;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.emargystudio.bohemeav0021.R;


public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    Fragment ReservationFragment;
    Fragment OrderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (savedInstanceState != null) {

            ReservationFragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.your_placeholder, ReservationFragment);
            ft.commit();
        }else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.your_placeholder, new ResHistoryListFragment(), "Reservation");
            ft.commit();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ReservationFragment = getSupportFragmentManager().findFragmentByTag("Reservation");
        OrderFragment = getSupportFragmentManager().findFragmentByTag("Order");
        if (ReservationFragment != null && ReservationFragment.isVisible()) {
            getSupportFragmentManager().putFragment(outState, "myFragmentName", ReservationFragment);
        }else if (OrderFragment != null && OrderFragment.isVisible()){
            getSupportFragmentManager().putFragment(outState, "myFragmentName", OrderFragment);
        }
        super.onSaveInstanceState(outState);
    }
}
