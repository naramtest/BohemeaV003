package com.emargystudio.bohemeav0021.Menu;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.emargystudio.bohemeav0021.Common;
import com.emargystudio.bohemeav0021.Model.FoodMenu;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;
import com.emargystudio.bohemeav0021.OrderDatabase.FoodViewModel;

import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.Heart;
import com.squareup.picasso.Picasso;


public class FoodDetailActivity extends AppCompatActivity {




    FoodMenu foodMenu;
    FoodMenu sqlfood;
    // Member variable for the Database
    private AppDatabase mDb;
    private String note = " ";


    //widget
    TextView  food_price, food_description , add_note , food_name;
    ImageView food_image ,like, like_red ;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;



    FoodViewModel viewModel;
    private Boolean mLikedByCurrentUser ;
    private GestureDetector mGestureDetector;
    private Heart mHeart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);


        initView();

        mDb = AppDatabase.getInstance(getApplicationContext());


        if (getIntent() != null){
            Bundle args =getIntent().getExtras();
            if (args != null)
            foodMenu = args.getParcelable("foodITem");
            if (foodMenu!=null)
            getFoodDetail(foodMenu);
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
                food_price.setText(String.format(getString(R.string.foodDetailActivity_food_price),newPrice));

            }
        });


        databaseOperations();
        mGestureDetector = new GestureDetector(FoodDetailActivity.this, new GestureListener());
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

        Toast.makeText(FoodDetailActivity.this, getString(R.string.foodDetailActivity_toast), Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        food_description = findViewById(R.id.food_description);
        food_price       = findViewById(R.id.food_price);
        food_image = findViewById(R.id.image_food);
        btnCart = findViewById(R.id.btnCart);
        numberButton = findViewById(R.id.number_button);
        add_note = findViewById(R.id.add_note);
        food_name = findViewById(R.id.food_name);
        like = findViewById(R.id.like_white);
        like_red = findViewById(R.id.like_red);
        mHeart = new Heart(like, like_red);
    }


    private void getFoodDetail(FoodMenu foodMenu){
        food_description.setText(foodMenu.getDescription());
        food_price.setText(String.format(getString(R.string.foodDetailActivity_food_price),foodMenu.getPrice()));
        Picasso.get().load(foodMenu.getImage_url()).into(food_image);
        food_name.setText(foodMenu.getName());

    }


    public void showDialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(FoodDetailActivity.this);
        alert.setTitle(getString(R.string.cart_update_dialog));
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = li.inflate(R.layout.alert_note,null);
        final EditText add_note_Edt = alertLayout.findViewById(R.id.addNoteEdt);
        alert.setView(alertLayout);
        alert.setNegativeButton(getString(R.string.cart_update_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton(R.string.foodDetailActivity_showDialog_addBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                note = add_note_Edt.getText().toString();

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }


    private void databaseOperations() {
        viewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
        if (foodMenu !=null){
            viewModel.getFood(foodMenu.getFood_id()).observe(this, new Observer<FoodMenu>() {
                @Override
                public void onChanged(@Nullable FoodMenu foodMenu) {
                    if (foodMenu!=null){
                        sqlfood = foodMenu;
                        mLikedByCurrentUser = true;

                    }else {
                        mLikedByCurrentUser = false;

                    }
                    likeTrigger();

                }
            });
        }


    }


    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            if (mLikedByCurrentUser){
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.foodDao().deleteFood(sqlfood);

                    }
                });
            }else {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.foodDao().insertFood(foodMenu);

                    }
                });
            }
            mHeart.toggleLike();
            return true;
        }

    }
    @SuppressLint("ClickableViewAccessibility")
    public void likeTrigger(){
        if(mLikedByCurrentUser){
            like.setVisibility(View.GONE);
            like_red.setVisibility(View.VISIBLE);
            like_red.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });

        }
        else{
            like.setVisibility(View.VISIBLE);
            like_red.setVisibility(View.GONE);
            like.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.getFood(foodMenu.getFood_id()).removeObservers(this);
    }

}
