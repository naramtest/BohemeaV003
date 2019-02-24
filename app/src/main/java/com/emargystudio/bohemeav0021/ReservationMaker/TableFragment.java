package com.emargystudio.bohemeav0021.ReservationMaker;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Common;
import com.emargystudio.bohemeav0021.Model.FoodOrder;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.Model.Table;
import com.emargystudio.bohemeav0021.OrderDatabase.AppDatabase;
import com.emargystudio.bohemeav0021.OrderDatabase.MainViewModel;
import com.emargystudio.bohemeav0021.R;
import com.emargystudio.bohemeav0021.ViewHolder.TableAdapter;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.emargystudio.bohemeav0021.Common.total;


public class TableFragment extends Fragment {

    private static final String TAG = "TableFragment";

    //widget
    TextView textView;
    TableAdapter tableAdapter;
    ProgressBar progressBar;



    //var
    private static final int NUM_COLUMNS = 2;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private Reservation reservation;
    private ArrayList<Integer> tableArray;
    private AppDatabase mDb;
    private List<FoodOrder> foodList;


    public TableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this datafragment
        if (savedInstanceState != null){
            Log.d(TAG, "onActivityCreated: "+mImageUrls.toString());
        }
        return inflater.inflate(R.layout.fragment_table, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        textView = view.findViewById(R.id.textView);
        progressBar = view.findViewById(R.id.progressBar_cyclic);
        tableArray = new ArrayList<>();
        mDb = AppDatabase.getInstance(getContext());
        foodList = new ArrayList<>();

        //try to get available tables from the query i use in data datafragment
        try {
            reservation = getReservationFromBundle();
            Log.d(TAG, "onViewCreated: "+reservation);
            reservationQuery();
            //initImageBitmaps();
        }catch (NullPointerException e){
            Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
        }
        loadListFood();
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NABILA.TTF");
        textView.setTypeface(face);
        initRecyclerView(view);



    }

    public void reservationQuery(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.reservation_query+ reservation.getYear()+"&month="+ reservation.getMonth()+"&day="+ reservation.getDay(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                JSONArray jsonArrayReservation =  jsonObject.getJSONArray("reservations");

                                Log.i("arrayReservation",jsonArrayReservation.toString());

                                for(int i = 0 ; i<jsonArrayReservation.length(); i++){
                                    JSONObject jsonObjectSingleRes = jsonArrayReservation.getJSONObject(i);
                                    Log.i("jsonObjectSingleRes",jsonObjectSingleRes.toString());

                                    double startHour = jsonObjectSingleRes.getDouble("hours");
                                    double endHour   = jsonObjectSingleRes.getDouble("end_hour");
                                    if (reservation.getStartHour() >= startHour && reservation.getStartHour() <= endHour){
                                        tableArray.add(jsonObjectSingleRes.getInt("table_id"));
                                    }
                                }
                                reservation.setTableArray(tableArray);
                                initImageBitmaps();

                            }else{
                                Toast.makeText(getContext(), "Please check your internet connection and try again later .... ", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onResponse: " + jsonObject.getString("message"));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"response error",Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: "+ error.getMessage());
                    }
                }

        );


        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
    }

    private void initImageBitmaps() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.tables_query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                JSONArray jsonObjectTables =  jsonObject.getJSONArray("tabels");

                                Log.i("arrayComments",jsonObjectTables.toString());

                                for(int i = 0 ; i<jsonObjectTables.length(); i++){
                                    JSONObject jsonObjectSingleTable = jsonObjectTables.getJSONObject(i);
                                    Log.i("jsonSingleStory",jsonObjectSingleTable.toString());

                                    Table table = new Table(jsonObjectSingleTable.getInt("table_number"),
                                            jsonObjectSingleTable.getInt("chair_number"),
                                            jsonObjectSingleTable.getString("table_image"));

                                    if (!reservation.getTableArray().contains(table.getTable_number())) {
                                        mImageUrls.add(table.getTable_image());
                                        mNames.add(String.valueOf(table.getTable_number()));

                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                                tableAdapter.notifyDataSetChanged();
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(),getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                    }
                }
        );
        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);



    }

    private void initRecyclerView(View view){

        Log.d(TAG, "initRecyclerView: "+foodList);
            RecyclerView recyclerView = view.findViewById(R.id.recycle_table);
            tableAdapter = new TableAdapter(getContext(),mNames,mImageUrls,reservation,foodList);
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            recyclerView.setAdapter(tableAdapter);
    }

    private Reservation getReservationFromBundle(){

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.reservation_bundle));
        }else{
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArrayList("Url",mImageUrls);
        Log.d(TAG, "onSaveInstanceState: "+outState.toString());
        outState.putStringArrayList("ID",mNames);
        super.onSaveInstanceState(outState);
    }


    private void loadListFood() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<FoodOrder>>() {
            @Override
            public void onChanged(@Nullable List<FoodOrder> foodOrders) {
                foodList = foodOrders;
                Common.isOrdered = true;
                tableAdapter.notifyDataSetChanged();
            }
        });

    }



}
