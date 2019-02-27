package com.emargystudio.bohemeav0021.Menu;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private String note = " ";


    //widget
    TextView  food_price, food_description , add_note;
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

        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                int newPrice = foodMenu.getPrice()*newValue;
                food_price.setText("Price: "+String.valueOf(newPrice)+" S.P");

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
        String food_image = foodMenu.getImage_url();
        String food_name = foodMenu.getName();


        final FoodOrder foodOrder = new FoodOrder(res_id,food_id,food_name,quantity,price,discount,food_image,note);
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

        Toast.makeText(FoodDetailActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        food_description = findViewById(R.id.food_description);
        food_price       = findViewById(R.id.food_price);
        food_image = findViewById(R.id.image_food);
        btnCart = findViewById(R.id.btnCart);
        numberButton = findViewById(R.id.number_button);
        add_note = findViewById(R.id.add_note);
    }


    private void getFoodDetail(FoodMenu foodMenu){
        food_description.setText(foodMenu.getDescription());
        food_price.setText("Price: "+String.valueOf(foodMenu.getPrice())+" S.P");
        Picasso.get().load(foodMenu.getImage_url()).into(food_image);
        collapsingToolbarLayout.setTitle(foodMenu.getName());

    }


    public void showDialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(FoodDetailActivity.this);
        alert.setTitle("Make your order special");
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = li.inflate(R.layout.alert_note,null);
        final EditText add_note_Edt = alertLayout.findViewById(R.id.addNoteEdt);
        alert.setView(alertLayout);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                note = add_note_Edt.getText().toString();

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }


}
