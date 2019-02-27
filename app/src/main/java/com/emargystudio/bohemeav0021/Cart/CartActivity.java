package com.emargystudio.bohemeav0021.Cart;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Common;
import com.emargystudio.bohemeav0021.HomeActivity;
import com.emargystudio.bohemeav0021.Menu.MenuActivity;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;
import com.emargystudio.bohemeav0021.OrderDatabase.MainViewModel;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ReservationMaker.ReservationActivity;
import com.emargystudio.bohemeav0021.ViewHolder.CartAdapter;
import com.emargystudio.bohemeav0021.ViewHolder.CartViewHolder;
import com.emargystudio.bohemeav0021.helperClasses.BottomNavigationViewHelper;
import com.emargystudio.bohemeav0021.helperClasses.RecyclerItemTouchHelper;
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

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = "CartActivity";
    private Context mContext = CartActivity.this;
    private static final int ACTIVITY_NUM = 3;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView txtTotalPrice;
    Button btnPlace , btnBackToMenu;
    private ConstraintLayout coordinatorLayout;
    LinearLayout fullView ;
    RelativeLayout emptyView;



    //recycler view var
    List<FoodOrder> foodList;
    CartAdapter cartAdapter;


    private AppDatabase mDb;
    Locale locale;
    NumberFormat fmt;
    int total1;




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

        btnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MenuActivity.class);
                startActivity(intent);
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

                                alertDone();
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
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        fullView = findViewById(R.id.full_view);
        emptyView = findViewById(R.id.emptyView);
        btnBackToMenu = findViewById(R.id.back_to_menu_btn);

        checkEmpty();



        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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
                total1=0;
                cartAdapter.setTasks(foodOrders);
                foodList = foodOrders;
                locale = new Locale("en","US");
                fmt = NumberFormat.getCurrencyInstance(locale);
                for (int i =0 ; i< foodOrders.size();i++ ){
                    total1 += foodOrders.get(i).getPrice()*foodOrders.get(i).getQuantity();
                }
                txtTotalPrice.setText(fmt.format(total1));
                Common.isOrdered = true;
                checkEmpty();
            }
        });

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof CartViewHolder) {
            // get the removed item name to display it in snack bar
            String name = foodList.get(viewHolder.getAdapterPosition()).getFood_name();

            // backup of removed item for undo purpose
            final FoodOrder deletedItem = foodList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            cartAdapter.removeItem(viewHolder.getAdapterPosition());
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.orderDao().deleteFood(deletedItem);
                }
            });



            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    cartAdapter.restoreItem(deletedItem, deletedIndex);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.orderDao().insertFood(deletedItem);
                        }
                    });
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }


    public void checkEmpty(){
        if (foodList !=null) {
            if (foodList.isEmpty()) {
                Common.isOrdered= false;
                emptyView.setVisibility(View.VISIBLE);
                fullView.setVisibility(View.GONE);

            } else {
                emptyView.setVisibility(View.GONE);
                fullView.setVisibility(View.VISIBLE);
            }
        }
    }


    public void alertDone(){
        AlertDialog.Builder alert = new AlertDialog.Builder(CartActivity.this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = li.inflate(R.layout.alert_done,null);
        alert.setView(alertLayout);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

    }


}
