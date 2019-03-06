package com.emargystudio.bohemeav0021.helperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;


import com.emargystudio.bohemeav0021.Cart.CartActivity;
import com.emargystudio.bohemeav0021.Cinema.CinemaActivity;
import com.emargystudio.bohemeav0021.HomeActivity;
import com.emargystudio.bohemeav0021.Menu.MenuActivity;
import com.emargystudio.bohemeav0021.Profile.ProfileActivity;
import com.emargystudio.bohemeav0021.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){


        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.setLabelVisibilityMode(1);
        bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);
        bottomNavigationViewEx.setTextVisibility(false);


    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.action_add:
                        Intent intent1 = new Intent(context, HomeActivity.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        //callingActivity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                        break;

                    case R.id.action_menu:
                        Intent intent2  = new Intent(context, MenuActivity.class);//ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        //callingActivity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                        break;

                    case R.id.action_cinema:
                        Intent intent3 = new Intent(context, CinemaActivity.class);//ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        //callingActivity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                        break;

                    case R.id.action_cart:
                        Intent intent4 = new Intent(context, CartActivity.class);//ACTIVITY_NUM = 3
                        context.startActivity(intent4);
                        //callingActivity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                        break;

                    case R.id.action_profile:
                        Intent intent5 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 4
                        context.startActivity(intent5);
                        //callingActivity.overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                        break;
                }
                return false;
            }
        });
    }
}
