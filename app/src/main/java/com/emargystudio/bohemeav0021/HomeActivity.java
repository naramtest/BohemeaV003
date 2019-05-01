package com.emargystudio.bohemeav0021;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Model.HomeImage;

import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;
import com.emargystudio.bohemeav0021.ReservationMaker.ReservationActivity;
import com.emargystudio.bohemeav0021.helperClasses.BottomNavigationViewHelper;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;

import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {


    //views
    Button btnMakeReservation;
    SliderLayout sliderLayout;

    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 0;
    AppDatabase mdb;
    ArrayList<HomeImage> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mdb = AppDatabase.getInstance(this);
        //app init
        if (!SharedPreferenceManger.getInstance(this).isUserLogggedIn()){
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.activity_home);
            if (!Common.isOrdered&&Common.total==0){
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mdb.orderDao().deleteAllFood();
                    }
                });
            }

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

            setupBottomNavigationView();
            images = new ArrayList<>();


            sliderLayout = findViewById(R.id.imageSlider);
            sliderLayout.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderLayout.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderLayout.setAutoScrolling(false);
            imageQuery();
        }
    }


    private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            DefaultSliderView sliderView = new DefaultSliderView(this);

            switch (i) {
                case 0:
                    if (images.size()!=0 &&!images.get(0).getImage_url().isEmpty()){
                        sliderView.setImageUrl(images.get(0).getImage_url());
                    }else {
                        sliderView.setImageUrl("https://www.incimages.com/uploaded_files/image/970x450/getty_509107562_2000133320009280346_351827.jpg");
                    }
                    break;
                case 1:
                    if (images.size()!=0 &&!images.get(1).getImage_url().isEmpty()){
                        sliderView.setImageUrl(images.get(1).getImage_url());
                    }
                    break;
                case 2:
                    if (images.size()!=0 &&!images.get(2).getImage_url().isEmpty()){
                        sliderView.setImageUrl(images.get(2).getImage_url());
                    }
                    break;
                case 3:
                    if (images.size()!=0 &&!images.get(3).getImage_url().isEmpty()){
                        sliderView.setImageUrl(images.get(3).getImage_url());
                    }
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);


            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }

    }



    public void imageQuery(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.home_images,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                JSONArray jsonObjectTables =  jsonObject.getJSONArray("images");

                                for(int i = 0 ; i<jsonObjectTables.length(); i++){
                                    JSONObject jsonimage = jsonObjectTables.getJSONObject(i);

                                    images.add(new HomeImage(jsonimage.getString("image_url"),
                                            jsonimage.getString("description"),
                                            jsonimage.getInt("position")));
                                }

                                setSliderViews();

                            }else{
                                setSliderViews();
                                Toast.makeText(HomeActivity.this,getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){
                            setSliderViews();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setSliderViews();
                        Toast.makeText(HomeActivity.this,getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                    }
                }
        );
        VolleyHandler.getInstance(HomeActivity.this).addRequetToQueue(stringRequest);
    }




    private void setupBottomNavigationView(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    //check if user logged in
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
