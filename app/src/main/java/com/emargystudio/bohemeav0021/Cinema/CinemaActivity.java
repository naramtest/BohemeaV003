package com.emargystudio.bohemeav0021.Cinema;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Model.Movie;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.MovieViewModel;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ViewHolder.HomeMoviePicsAdapter;
import com.emargystudio.bohemeav0021.helperClasses.BottomNavigationViewHelper;
import com.emargystudio.bohemeav0021.helperClasses.CardPagerAdapter;
import com.emargystudio.bohemeav0021.helperClasses.MovieItemClickListener;
import com.emargystudio.bohemeav0021.helperClasses.RecyclerTouchListener;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.emargystudio.bohemeav0021.helperClasses.ZoomOutPageTransformer;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CinemaActivity extends AppCompatActivity implements MovieItemClickListener {

    private static final String TAG = "HomeFragment";

    private List<Movie> popularMovies = new ArrayList<>();
    private List<Movie> myListImagesUrl = new ArrayList<>();
    private HomeMoviePicsAdapter popularAdapter , myListAdapter;
    private RecyclerView popularRecyclerView, myListRV ;
    private RelativeLayout my_list_container;
    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private AppDatabase mDb;
    MovieViewModel viewModel;


    Button category ;


    private Context mContext = CinemaActivity.this;
    private static final int ACTIVITY_NUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema);

        setupBottomNavigationView();

        mViewPager = findViewById(R.id.viewPager);
        category = findViewById(R.id.category);
        my_list_container = findViewById(R.id.my_list_container);


        newMovieViewPagerSetup();
        popularMoviesInit();
        myListRvInit();


        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CinemaActivity.this, CinemaCategoryActivity.class);
                startActivity(intent);
            }
        });

    }

    private void newMovieViewPagerSetup() {
        mCardAdapter = new CardPagerAdapter(this);
        newMovieInit();
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer(true));
        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.dimen_20));
        mViewPager.setOffscreenPageLimit(3);
        mDb = AppDatabase.getInstance(CinemaActivity.this);
        viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

    }

    private void popularMoviesInit() {
        popularRecyclerView = findViewById(R.id.popular_movies_recycler_view);
        popularAdapter = new HomeMoviePicsAdapter(popularMovies);
        popularRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CinemaActivity.this,LinearLayoutManager.HORIZONTAL, false);
        popularRecyclerView.setLayoutManager(mLayoutManager);
        popularRecyclerView.setAdapter(popularAdapter);
        popularRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(CinemaActivity.this, popularRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                goToMovieDetail(popularMovies.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        new GravitySnapHelper(Gravity.START).attachToRecyclerView(popularRecyclerView);
        loadPopularImages();
    }
    private void myListRvInit(){
        myListRV = findViewById(R.id.my_list_recycler_view);
        myListAdapter = new HomeMoviePicsAdapter(myListImagesUrl);
        loadMyListImages();
        popularRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CinemaActivity.this,LinearLayoutManager.HORIZONTAL, false);
        myListRV.setLayoutManager(mLayoutManager);
        myListRV.setAdapter(myListAdapter);
        new GravitySnapHelper(Gravity.START).attachToRecyclerView(myListRV);
        myListRV.addOnItemTouchListener(new RecyclerTouchListener(CinemaActivity.this, myListRV, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                goToMovieDetail(myListImagesUrl.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }
    private void loadPopularImages(){

        popularMovies.clear();
        String popular_movie_query = "http://naramalkoht.ml/bohemea/popular_movie_query.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, popular_movie_query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray moviesArray = jsonObject.getJSONArray("movies");

                            for (int i=0;i< moviesArray.length();i++){
                                JSONObject movieObject = moviesArray.getJSONObject(i);
                                Movie movie = new Movie(movieObject.getInt("tmdb_id"),
                                        movieObject.getString("title"),
                                        movieObject.getString("genres"),
                                        movieObject.getString("release_date"),
                                        movieObject.getString("poster_path"),
                                        movieObject.getString("backdrop_path"),
                                        movieObject.getString("overview"),
                                        movieObject.getInt("vote_average"),
                                        movieObject.getInt("runtime"));

                                popularMovies.add(movie);
                            }
                            popularAdapter.notifyDataSetChanged();
                            mViewPager.setCurrentItem(1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                }
        );//end of string Request

        VolleyHandler.getInstance(CinemaActivity.this).addRequetToQueue(stringRequest);


    }
    private void loadMyListImages(){

        if (myListImagesUrl.isEmpty()){
            my_list_container.setVisibility(View.GONE);
        }

        viewModel.getTasks().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                myListImagesUrl = movies;
                if (!myListImagesUrl.isEmpty()){
                    my_list_container.setVisibility(View.VISIBLE);
                }
                myListAdapter.setImagesUrl(movies);
                myListAdapter.notifyDataSetChanged();

            }
        });
    }
    private void newMovieInit(){

        String new_movies_query = "http://naramalkoht.ml/bohemea/new_movies_query.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new_movies_query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray moviesArray = jsonObject.getJSONArray("movies");

                            for (int i=0;i< moviesArray.length();i++){
                                JSONObject movieObject = moviesArray.getJSONObject(i);
                                Movie movie = new Movie(movieObject.getInt("tmdb_id"),
                                        movieObject.getString("title"),
                                        movieObject.getString("genres"),
                                        movieObject.getString("release_date"),
                                        movieObject.getString("poster_path"),
                                        movieObject.getString("backdrop_path"),
                                        movieObject.getString("overview"),
                                        movieObject.getInt("vote_average"),
                                        movieObject.getInt("runtime"));

                                mCardAdapter.addCardItem(movie);
                            }
                            mCardAdapter.notifyDataSetChanged();
                            mViewPager.setCurrentItem(1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                }
        );//end of string Request

        VolleyHandler.getInstance(CinemaActivity.this).addRequetToQueue(stringRequest);

    }

    @Override
    public void onMovieItemClick(int pos, Movie movie, ImageView shareImageView) {
        goToMovieDetail(movie);
    }

    private void goToMovieDetail(Movie movie) {

        Intent intent = new Intent(CinemaActivity.this,MovieDetailsActivity.class);
        intent.putExtra("movie",movie);
        startActivity(intent);
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

}
