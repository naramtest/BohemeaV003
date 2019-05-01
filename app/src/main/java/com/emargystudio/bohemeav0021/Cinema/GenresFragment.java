package com.emargystudio.bohemeav0021.Cinema;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Model.Movie;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.MovieItemClickListener;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.emargystudio.bohemeav0021.helperClasses.ZoomOutPageTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GenresFragment extends Fragment implements MovieItemClickListener {



    ViewPager mViewPager;
    GenresPagerAdapter genresPagerAdapter;
    String genre , searhWord ;
    TextView movie_title ;




    public GenresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genres, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {



        genre = getMovieFromBundle();
        mViewPager = view.findViewById(R.id.viewPager);
        movie_title = view.findViewById(R.id.movie_title);



        genresPagerAdapter = new GenresPagerAdapter(this);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer(true));
        mViewPager.setAdapter(genresPagerAdapter);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.dimen_20));
        mViewPager.setOffscreenPageLimit(3);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            if (bundle.getString("Genre")!= null){
                genre = bundle.getString("Genre");
                movieQueryByGenres(genre);
            }else if (bundle.getString("searchWord")!=null){
                searhWord = bundle.getString("searchWord");
                searchQuery(searhWord);
            }
        }


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                setupMovieDetails(genresPagerAdapter.getDate(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }

    private void movieQueryByGenres(String genres){
        String url = "http://naramalkoht.ml/bohemea/genres_query.php?genre="+genres;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray moviesArray = jsonObject.getJSONArray("movies");

                            for (int i=0;i< moviesArray.length();i++) {
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

                                genresPagerAdapter.addCardItem(movie);
                                if (i==0){
                                    setupMovieDetails(movie);
                                }
                            }
                            genresPagerAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            Toast.makeText(getContext(), getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                    }
                }
        );//end of string Request

        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }

    private void setupMovieDetails(Movie movie) {
        movie_title.setText(movie.getTitle());

    }

    private String getMovieFromBundle(){
        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getString("Genre");
        }else{
            return null;
        }
    }
    private void searchQuery(String searchWord) {
        String url = "http://naramalkoht.ml/bohemea/movie_searh.php?movie_word="+searchWord;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray moviesArray = jsonObject.getJSONArray("movies");

                            for (int i=0;i< moviesArray.length();i++) {
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

                                genresPagerAdapter.addCardItem(movie);
                                if (i==0){
                                    setupMovieDetails(movie);
                                }
                            }
                            genresPagerAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(getContext(), getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                    }
                }
        );//end of string Request
        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }

    @Override
    public void onMovieItemClick(int pos, Movie movie, ImageView shareImageView) {

        Intent intent = new Intent(getContext(),MovieDetailsActivity.class);
        intent.putExtra("movie",movie);
        startActivity(intent);
    }
}
