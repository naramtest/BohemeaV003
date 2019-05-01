package com.emargystudio.bohemeav0021.Cinema;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CinemaCategoryActivity extends AppCompatActivity  {


    private static final String TAG = "CinemaCategoryActivity";
    String[] genres = {"Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary",
            "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Mystery", "Romance",
            "Science Fiction", "TV Movie", "Thriller", "War", "Western"};
    private List<String> moviesTitle = new ArrayList<>();

    MaterialSearchView searchView;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_category);

        Toolbar toolbar = findViewById(R.id.toolBar);
        tabLayout = findViewById(R.id.sliding_tabs);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        suggestionQuery();
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery(query);
                return false;

            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        tabLayoutInit();
        switchFragment(genres[0]);




    }

    private void tabLayoutInit() {
        for (String genre : genres) {
            tabLayout.addTab(tabLayout.newTab().setText(genre));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchFragment(genres[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void switchFragment(String genre) {
        Bundle bundle = new Bundle();
        bundle.putString("Genre", genre);
        GenresFragment fragment = new GenresFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.your_placeholder, fragment)
                .addToBackStack(TAG)
                .commit();
    }

    //search methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
    private void searchQuery(String searchWord) {
        Bundle bundle = new Bundle();
        bundle.putString("searchWord", searchWord);
        GenresFragment fragment = new GenresFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.your_placeholder, fragment)
                .addToBackStack(TAG)
                .commit();
    }
    private void suggestionQuery(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.movies_suggestion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonMovies = jsonObject.getJSONArray("movies");
                            for (int i = 0; i<jsonMovies.length();i++){
                                JSONObject item = jsonMovies.getJSONObject(i);
                                String title = item.getString("title");
                                moviesTitle.add(title);
                            }

                            String[] mStringArray = new String[moviesTitle.size()];
                            mStringArray = moviesTitle.toArray(mStringArray);

                            searchView.setSuggestions(mStringArray);
                            searchView.showSuggestions();
                            searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String query1 = (String) parent.getItemAtPosition(position);
                                    String query = query1.replace(" ","+");
                                    searchQuery(query);
                                    searchView.closeSearch();
                                }
                            });

                        } catch (JSONException e) {
                            Toast.makeText(CinemaCategoryActivity.this, getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CinemaCategoryActivity.this, getString(R.string.internet_off), Toast.LENGTH_SHORT).show();
                    }
                }
        );//end of string Request

        VolleyHandler.getInstance(CinemaCategoryActivity.this).addRequetToQueue(stringRequest);
    }
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

}
