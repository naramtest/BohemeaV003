package com.emargystudio.bohemeav0021.Cart;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Common;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;
import com.emargystudio.bohemeav0021.OrderDatabase.MainViewModel;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ReservationMaker.ReservationActivity;
import com.emargystudio.bohemeav0021.ViewHolder.CartAdapter;
import com.emargystudio.bohemeav0021.helperClasses.BottomNavigationViewHelper;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.google.gson.Gson;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.emargystudio.bohemeav0021.Common.res_id;
import static com.emargystudio.bohemeav0021.Common.total;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CartActivity";
    private Context mContext = CartActivity.this;
    private static final int ACTIVITY_NUM = 3;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView txtTotalPrice;
    Button btnPlace;

    List<FoodOrder> foodList;
    CartAdapter cartAdapter;
    private AppDatabase mDb;
    Locale locale;
    NumberFormat fmt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initView();
        loadListFood();
        setupBottomNavigationView();

        mDb = AppDatabase.getInstance(getApplicationContext());
        foodList = new ArrayList<>();


        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.res_id != 0){
                    sendOrder();
                }else {
                    Intent intent = new Intent(CartActivity.this, ReservationActivity.class);
                    startActivity(intent);
                }
            }
        });





    }

    private void sendOrder() {
                Gson gson=new Gson();
                final String newDataArray=gson.toJson(foodList);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.test,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Common.total = 0;
                                txtTotalPrice.setText(fmt.format(total));
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDb.orderDao().deleteAllFood();
                                    }
                                });

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                error.getMessage();
                            }
                        }
                ){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> param=new HashMap<>();
                        param.put("array",newDataArray);
                        param.put("total", String.valueOf(total));
                        param.put("res_id",String.valueOf(res_id));// array is key which we will use on server side

                        return param;
                    }
                };//end of string Request

                VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);



            }



    private void initView() {
        txtTotalPrice = findViewById(R.id.total);
        btnPlace =findViewById(R.id.btnPlaceOrder);
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        cartAdapter = new CartAdapter(this);
        recyclerView.setAdapter(cartAdapter);

        if (Common.res_id == 0){
            btnPlace.setText("Make a Reservation First");
        }
    }


    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    private void loadListFood() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<FoodOrder>>() {
            @Override
            public void onChanged(@Nullable List<FoodOrder> foodOrders) {
                cartAdapter.setTasks(foodOrders);
                foodList = foodOrders;
                locale = new Locale("en","US");
                fmt = NumberFormat.getCurrencyInstance(locale);
                txtTotalPrice.setText(fmt.format(total));
                Common.isOrdered = true;
            }
        });

    }

}
