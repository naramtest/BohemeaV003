package com.emargystudio.bohemeav0021.Cinema;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Model.Movie;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.AppExecutors;
import com.emargystudio.bohemeav0021.OrderDatabase.MovieViewModel;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ReservationMaker.ReservationActivity;
import com.emargystudio.bohemeav0021.ViewHolder.ScreenShotsAdapter;
import com.emargystudio.bohemeav0021.helperClasses.Heart;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {


    Movie movie;
    ImageView imageView , like, like_red , back_button;
    RatingBar ratingBar;
    TextView movie_name , movie_genres , release_date ;
    TextView over_view , runtime;
    RecyclerView screenshots_rv;
    Movie sqlMovie;
    FloatingActionButton btnTicket;



    ScreenShotsAdapter screenShotsAdapter;
    private AppDatabase mDb;
    MovieViewModel viewModel;
    private Boolean mLikedByCurrentUser ;
    private GestureDetector mGestureDetector;
    private Heart mHeart;


    private List<String> screenshotsUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (getIntent()!=null){
            movie = getIntent().getParcelableExtra("movie");
        }

        initViews();
        databaseOperations();
        setupViewsInfo();

        mDb = AppDatabase.getInstance(MovieDetailsActivity.this);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailsActivity.this, ReservationActivity.class);
                intent.putExtra("movie_name",movie.getTitle());
                startActivity(intent);
            }
        });
    }

    private void databaseOperations() {
        viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        if (movie !=null){
            viewModel.getMovie(movie.getTmdb_id()).observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(@Nullable Movie movie) {
                    if (movie!=null){
                        sqlMovie = movie;
                        mLikedByCurrentUser = true;
                    }else {
                        mLikedByCurrentUser = false;
                    }
                    likeTrigger();
                }
            });
        }
    }

    private void initViews() {
        imageView = findViewById(R.id.movie_image);
        ratingBar = findViewById(R.id.movieDetailsRating);
        movie_genres = findViewById(R.id.movie_genres);
        movie_name   = findViewById(R.id.movie_name);
        release_date = findViewById(R.id.release_date);
        over_view = findViewById(R.id.overview);
        screenshots_rv = findViewById(R.id.screenshots_rv);
        like = findViewById(R.id.like_white);
        like_red = findViewById(R.id.like_red);
        btnTicket = findViewById(R.id.btnTicket);
        runtime = findViewById(R.id.length);
        back_button = findViewById(R.id.back_button);
        mHeart = new Heart(like, like_red);
        mGestureDetector = new GestureDetector(MovieDetailsActivity.this, new GestureListener());
    }

    private void setupViewsInfo() {
        try {

            ViewCompat.setTransitionName(imageView,movie.getTitle());
            Picasso.get().load("https://image.tmdb.org/t/p/w500"+movie.getBackdrop_path()).fit().centerCrop()
                    .into(imageView);
            ratingBar.setRating(movie.getVote_average()/2.0f);
            movie_name.setText(movie.getTitle());
            String genres1 = movie.getGenres().replace(",",", ");
            String genres = method(genres1);
            movie_genres.setText(genres);
            release_date.setText(movie.getRelease_date());
            over_view.setText(movie.getOverview());
            runtime.setText(String.valueOf(movie.getRuntime()));
            setupRecyclerView(movie);

        }catch (NullPointerException e){

            Toast.makeText(MovieDetailsActivity.this, getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
        }
    }



    //delete last index of genres
    public String method(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 2);
        }
        return str;
    }

    //query screenshots images from tmdb
    public void screenShotsQuery(int id){
        String screenshots_query = "https://api.themoviedb.org/3/movie/"+id+"?api_key=a6ca406a37e67226011de7b41910b0a6&append_to_response=images";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, screenshots_query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject images = jsonObject.getJSONObject("images");
                            JSONArray backdrops = images.getJSONArray("backdrops");
                            for (int i=0;i<backdrops.length();i++){
                                if (i<10) {
                                    JSONObject backdrop = backdrops.getJSONObject(i);
                                    screenshotsUrl.add(backdrop.getString("file_path"));
                                    Collections.shuffle(screenshotsUrl);
                                }
                            }

                            screenShotsAdapter.notifyDataSetChanged();




                        } catch (JSONException e) {
                            Toast.makeText(MovieDetailsActivity.this, getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MovieDetailsActivity.this, getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                    }
                }
        );//end of string Request

        VolleyHandler.getInstance(MovieDetailsActivity.this).addRequetToQueue(stringRequest);
    }
    private void setupRecyclerView( Movie movie) {
        screenShotsAdapter = new ScreenShotsAdapter(screenshotsUrl);
        screenshots_rv.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MovieDetailsActivity.this,LinearLayoutManager.HORIZONTAL, false);
        screenshots_rv.setLayoutManager(mLayoutManager);
        screenshots_rv.setAdapter(screenShotsAdapter);
        new GravitySnapHelper(Gravity.START).attachToRecyclerView(screenshots_rv);
        screenShotsQuery(movie.getTmdb_id());
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.getMovie(movie.getTmdb_id()).removeObservers(this);
    }


    //like system
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
                        mDb.movieDao().deleteFood(sqlMovie);

                    }
                });
            }else {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.movieDao().insertFood(movie);

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
}
