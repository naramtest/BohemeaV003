package com.emargystudio.bohemeav0021.Menu;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.emargystudio.bohemeav0021.Common;
import com.emargystudio.bohemeav0021.Model.FoodMenu;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;
import com.emargystudio.bohemeav0021.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class FoodDetailActivity extends AppCompatActivity {

    private static final String TAG = "FoodDetailActivity";


    FoodMenu foodMenu;
    // Member variable for the Database
    private AppDatabase mDb;


    //widget
    TextView food_name , food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);


        initView();

        mDb = AppDatabase.getInstance(getApplicationContext());

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        if (getIntent() != null){
            Bundle args =getIntent().getExtras();
            foodMenu = args.getParcelable("foodITem");
            getFoodDetail(foodMenu);
            Log.d(TAG, "onCreate: " + foodMenu);
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToCart();
            }
        });
    }

    private void addToCart() {
        int food_id = foodMenu.getId();
        int discount = foodMenu.getDiscount();
        int quantity = Integer.parseInt(numberButton.getNumber());
        int price = foodMenu.getPrice();
        Common.total = (Common.total)+(price*quantity);
        int res_id = Common.res_id;
        String food_name = foodMenu.getName();

        final FoodOrder foodOrder = new FoodOrder(res_id,food_id,food_name,quantity,price,discount);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                mDb.orderDao().insertFood(foodOrder);
                if (!Common.isOrdered ){
                    Common.isOrdered = true;

                }

                finish();
            }

        });
    }

    private void initView() {
        food_name = findViewById(R.id.food_name);
        food_description = findViewById(R.id.food_description);
        food_price       = findViewById(R.id.food_price);
        food_image = findViewById(R.id.image_food);
        btnCart = findViewById(R.id.btnCart);
        numberButton = findViewById(R.id.number_button);
    }


    private void getFoodDetail(FoodMenu foodMenu){
        food_name.setText(foodMenu.getName());
        food_description.setText(foodMenu.getDescription());
        food_price.setText(String.valueOf(foodMenu.getPrice()));
        Picasso.get().load(foodMenu.getImage_url()).into(food_image);

    }


}