package com.emargystudio.bohemeav0021.Menu;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Cart.CartActivity;
import com.emargystudio.bohemeav0021.Common;
import com.emargystudio.bohemeav0021.HomeActivity;
import com.emargystudio.bohemeav0021.InterFace.ItemClickListener;
import com.emargystudio.bohemeav0021.MainActivity;
import com.emargystudio.bohemeav0021.Model.FoodCategory;
import com.emargystudio.bohemeav0021.Model.FoodMenu;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;
import com.emargystudio.bohemeav0021.OrderDatabase.FoodViewModel;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ViewHolder.FoodCategoryAdapter;
import com.emargystudio.bohemeav0021.ViewHolder.FoodMenuAdapter;
import com.emargystudio.bohemeav0021.helperClasses.BottomNavigationViewHelper;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {


    private Context mContext = MenuActivity.this;


    private static final int ACTIVITY_NUM = 1;
    private int currentCategory = 0;
    //widget
    CardView categoryCardView ;
    TextView categorySelected;
    ProgressBar menuPb;



    //category arrays
    ArrayList<FoodCategory> foodCategories = new ArrayList<>();
    FoodCategoryAdapter foodCategoryAdapter;
    //food arrays
    ArrayList<FoodMenu> foodMenus = new ArrayList<>();
    FoodMenuAdapter foodMenuAdapter;
    private AppDatabase mDb;
    FoodViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        categoryCardView = findViewById(R.id.categoryCardView);
        categorySelected = findViewById(R.id.categorySelected);
        menuPb = findViewById(R.id.menuPb);


        categoryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });
        mDb = AppDatabase.getInstance(MenuActivity.this);
        viewModel = ViewModelProviders.of(this).get(FoodViewModel.class);

        setupBottomNavigationView();
        categoryQuery();
        initRecyclerView();
    }


    private void setupBottomNavigationView(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    public void categoryQuery(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.food_category_query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){
                                JSONArray jsonObjectCategory =  jsonObject.getJSONArray("categorys");
                                foodCategories.add(new FoodCategory(-1,"Favorite"));
                                for(int i = 0 ; i<jsonObjectCategory.length(); i++){
                                    JSONObject jsonObjectSingleCategory = jsonObjectCategory.getJSONObject(i);
                                    Log.i("jsonSingleStory",jsonObjectSingleCategory.toString());

                                    foodCategories.add(new FoodCategory(jsonObjectSingleCategory.getInt("id"),
                                            jsonObjectSingleCategory.getString("name")));
                                    if (i ==0){
                                        currentCategory = 1;
                                        foodQuery(foodCategories.get(currentCategory).getId());
                                        categorySelected.setText(foodCategories.get(currentCategory).getName());
                                    }
                                }
                            }else{
                                Toast.makeText(MenuActivity.this,getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MenuActivity.this,getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                    }
                }
        );
        VolleyHandler.getInstance(MenuActivity.this).addRequetToQueue(stringRequest);
    }

    
    public void categoryDialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(MenuActivity.this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = li.inflate(R.layout.category_list_dialog, null);
        RecyclerView recyclerView = alertLayout.findViewById(R.id.categoryRecycler);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();
        foodCategoryAdapter = new FoodCategoryAdapter(MenuActivity.this, foodCategories, currentCategory, new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                currentCategory = position;
                if (position!=0){
                    foodQuery(foodCategories.get(currentCategory).getId());
                }else {
                    favorite();
                }
                categorySelected.setText(foodCategories.get(currentCategory).getName());
                dialog.dismiss();
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
        recyclerView.setAdapter(foodCategoryAdapter);
    }

    private void favorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (foodMenus!=null){
                    foodMenus.clear();
                }
                List<FoodMenu> foodMenuList = mDb.foodDao().loadAllFoodsAdapter();
                foodMenus.addAll(foodMenuList);
                foodMenuAdapter.notifyDataSetChanged();
            }
        });
    }

    public void foodQuery(int category_id){

        menuPb.setVisibility(View.VISIBLE);
        if (foodMenus!=null){
            foodMenus.clear();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.food_menu_query+category_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                JSONArray jsonObjectCategory =  jsonObject.getJSONArray("food_menu");


                                for(int i = 0 ; i<jsonObjectCategory.length(); i++){
                                    JSONObject jfood = jsonObjectCategory.getJSONObject(i);


                                    foodMenus.add(new FoodMenu(jfood.getInt("id"),
                                            jfood.getInt("category_id"),
                                            jfood.getString("name"),
                                            jfood.getString("image_url"),
                                            jfood.getString("description"),
                                            jfood.getInt("price"),
                                            jfood.getInt("discount")));
                                }
                                menuPb.setVisibility(View.GONE);
                                foodMenuAdapter.notifyDataSetChanged();
                            }else{
                                menuPb.setVisibility(View.GONE);
                                Toast.makeText(MenuActivity.this,getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MenuActivity.this,getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                    }
                }
        );
        VolleyHandler.getInstance(MenuActivity.this).addRequetToQueue(stringRequest);

    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.food_list);
        foodMenuAdapter = new FoodMenuAdapter(MenuActivity.this, foodMenus, new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                Intent intent = new Intent(MenuActivity.this,FoodDetailActivity.class);
                intent.putExtra("foodITem",foodMenus.get(position));
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(foodMenuAdapter);
    }

    @Override
    public void onBackPressed() {
        if (Common.isOrdered){
            AlertDialog.Builder alert = new AlertDialog.Builder(MenuActivity.this);
            alert.setTitle("Sending Order");
            alert.setMessage("To send your order please go to Your Cart");
            alert.setPositiveButton("Cart", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MenuActivity.this,CartActivity.class);
                    startActivity(intent);
                }
            });
            alert.setNegativeButton("later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MenuActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentCategory == 0){
            viewModel.getTasks().observe(MenuActivity.this, new Observer<List<FoodMenu>>() {
                @Override
                public void onChanged(@Nullable List<FoodMenu> foodMenus1) {
                    if (foodMenus!=null){
                        foodMenus.clear();
                    }
                    assert foodMenus1 != null;
                    assert foodMenus != null;
                    foodMenus.addAll(foodMenus1);
                    foodMenuAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
