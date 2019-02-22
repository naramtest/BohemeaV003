package com.emargystudio.bohemeav0021.Menu;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.InterFace.ItemClickListener;
import com.emargystudio.bohemeav0021.MainActivity;
import com.emargystudio.bohemeav0021.Model.FoodCategory;
import com.emargystudio.bohemeav0021.Model.FoodMenu;
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

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";
    private Context mContext = MenuActivity.this;
    private Fragment fragment;



    private static final int ACTIVITY_NUM = 1;
    private int currentCategory = 0;

    //widget
    CardView categoryCardView ;
    TextView categorySelected;
    RecyclerView foodList;

    //var
    //category arrays
    ArrayList<FoodCategory> foodCategories = new ArrayList<>();
    FoodCategoryAdapter foodCategoryAdapter;
    //food arrays
    ArrayList<FoodMenu> foodMenus = new ArrayList<>();
    FoodMenuAdapter foodMenuAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        categoryCardView = findViewById(R.id.categoryCardView);
        categorySelected = findViewById(R.id.categorySelected);

        categoryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        setupBottomNavigationView();
        categoryQuery();
        initRecyclerView();
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

    public void categoryQuery(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.food_category_query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                JSONArray jsonObjectCategory =  jsonObject.getJSONArray("categorys");


                                for(int i = 0 ; i<jsonObjectCategory.length(); i++){
                                    JSONObject jsonObjectSingleCategory = jsonObjectCategory.getJSONObject(i);
                                    Log.i("jsonSingleStory",jsonObjectSingleCategory.toString());

                                    foodCategories.add(new FoodCategory(jsonObjectSingleCategory.getInt("id"),
                                            jsonObjectSingleCategory.getString("name")));
                                    if (i ==0){
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
                Log.d(TAG, "onClick: "+currentCategory);
                categorySelected.setText(foodCategories.get(currentCategory).getName());
                dialog.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
        recyclerView.setAdapter(foodCategoryAdapter);
    }

    public void foodQuery(int category_id){

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
                                foodMenuAdapter.notifyDataSetChanged();
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
}
