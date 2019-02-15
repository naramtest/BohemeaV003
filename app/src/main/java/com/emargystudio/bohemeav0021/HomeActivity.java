package com.emargystudio.bohemeav0021;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.emargystudio.bohemeav0021.ReservationMaker.ReservationActivity;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;

import com.facebook.login.LoginManager;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    //views
    Button btnMakeReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SharedPreferenceManger.getInstance(this).isUserLogggedIn()){

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.activity_home);


            //widget
            btnMakeReservation = findViewById(R.id.btn_make_reservation);


            //clickListeners
            btnMakeReservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reservationIntent = new Intent(HomeActivity.this, ReservationActivity.class);
                    startActivity(reservationIntent);
                }
            });

        }
    }

    private void logUserOut(){
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        // logging out of Facebook
        LoginManager.getInstance().logOut();
        // logging out of Parse
        ParseUser.logOut();
        SharedPreferenceManger.getInstance(this).logUserOut();
        Toast.makeText(this, "Good bye .....", Toast.LENGTH_SHORT).show();

    }




    @Override
    protected void onStart() {
        super.onStart();
        if (!SharedPreferenceManger.getInstance(this).isUserLogggedIn()){

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }


}