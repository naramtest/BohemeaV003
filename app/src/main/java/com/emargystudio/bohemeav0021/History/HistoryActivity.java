package com.emargystudio.bohemeav0021.History;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.emargystudio.bohemeav0021.R;


public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new ResHistoryListFragment(),"Data");
        ft.commit();
    }



}
