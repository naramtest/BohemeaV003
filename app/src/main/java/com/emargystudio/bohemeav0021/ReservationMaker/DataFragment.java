package com.emargystudio.bohemeav0021.ReservationMaker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.TimePicker;

import com.emargystudio.bohemeav0021.helperClasses.CommonReservation;
import com.emargystudio.bohemeav0021.HomeActivity;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Objects;


public class DataFragment extends Fragment {



    TextView txtData;
    EditText edtDate , edtHour , edtChairs;
    FloatingActionButton nextFAB;
    TextInputLayout dataLayout,hourLayout,chairLayout;
    ConstraintLayout constraintLayoutl;
    Toolbar toolbar;
    String movie_name;


    //var
    int chosenYear,chosenMonth,chosenDay;
    double chosenStartHour , chosenEndHour;
    int chosenChair;
    Reservation reservation = new Reservation();

    TableFragment tableFragment;

    public DataFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {



        //widget
        initView(view);

        //var
        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay   = calendar.get(Calendar.DAY_OF_MONTH);
        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);
        tableFragment = new TableFragment();

        //change fonts
        if (getActivity()!=null){
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NABILA.TTF");
            txtData.setTypeface(face);
        }



        Bundle bundle = this.getArguments();
        if (bundle!=null){
           movie_name = bundle.getString("movie_name");
           reservation.setMovie_name(movie_name);

        }


        // listener
        listener(currentYear, currentMonth, currentDay, currentHour, currentMinute);

    }

    private void listener(final int currentYear, final int currentMonth, final int currentDay, final int currentHour, final int currentMinute) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        nextFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               nextFragment();
            }
        });

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(currentYear,currentMonth,currentDay);

            }
        });

        edtHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(currentHour,currentMinute);
            }
        });

        edtChairs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edtChairs.getText().toString().isEmpty()) {
                    chosenChair = Integer.parseInt(edtChairs.getText().toString());
                    chairLayout.setErrorEnabled(false);


                }
            }
        });

        edtChairs.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                   nextFragment();
                    return true;
                }
                return false;
            }
        });
    }

    private void initView(@NonNull View view) {
        edtDate = view.findViewById(R.id.edtDate);
        edtHour = view.findViewById(R.id.edtHour);
        edtChairs = view.findViewById(R.id.edtChairs);
        txtData = view.findViewById(R.id.textView);
        nextFAB = view.findViewById(R.id.nextFAB);
        dataLayout = view.findViewById(R.id.dateLayout);
        hourLayout = view.findViewById(R.id.hourLayout);
        chairLayout = view.findViewById(R.id.chairLayout);
        constraintLayoutl = view.findViewById(R.id.constraint1);
        toolbar = view.findViewById(R.id.tool_bar);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        setupReservationModel();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.reservation_bundle), reservation);
        outState.putBundle("args",args);
        super.onSaveInstanceState(outState);
    }

    public void datePicker(int currentYear, int currentMonth, int currentDay){

        DatePickerDialog dialog = new DatePickerDialog(Objects.requireNonNull(getContext()), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                chosenYear = year;
                chosenMonth = month+1;
                chosenDay = dayOfMonth;
                edtDate.setText(chosenYear +"-"+chosenMonth+"-"+chosenDay);
                dataLayout.setErrorEnabled(false);

            }
        }, currentYear, currentMonth, currentDay);
        dialog.show();

    }

    public void timePicker(int currentHour,int currentMinute){

        TimePickerDialog timeDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                chosenStartHour = formatStartingHour(hourOfDay,minute);
                edtHour.setText(CommonReservation.changeHourFormat(chosenStartHour));
                chosenEndHour = chosenStartHour + 2;
                hourLayout.setErrorEnabled(false);

            }
        },currentHour,currentMinute,false);
        timeDialog.show();
    }

    public double formatStartingHour(int hour , int minute){
        double d = minute /100.00;
        double h = hour+d;
        return round(h,2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public void setupReservationModel(){

        reservation.setYear(chosenYear);
        reservation.setMonth(chosenMonth);
        reservation.setDay(chosenDay);
        reservation.setStartHour(chosenStartHour);
        reservation.setEnd_hour(chosenEndHour);
        reservation.setChairNumber(chosenChair);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null){
            Bundle bundle = savedInstanceState.getBundle("args");
            if (bundle != null) {
                reservation = bundle.getParcelable(getString(R.string.reservation_bundle));
                if (reservation!=null) {
                    chosenYear = reservation.getYear();
                    chosenMonth = reservation.getMonth();
                    chosenDay = reservation.getDay();
                    chosenStartHour = reservation.getStartHour();
                    chosenEndHour = reservation.getEnd_hour();
                    chosenChair = reservation.getChairNumber();
                }
            }

        }
        super.onActivityCreated(savedInstanceState);
    }

    public void nextFragment(){
        if (chosenYear == 0 || chosenMonth == 0 || chosenDay == 0) {
            dataLayout.setError(getString(R.string.table_emptyEdt_error_data));


        }else if (chosenStartHour == 0 && chosenEndHour == 0 ) {

            hourLayout.setError(getString(R.string.table_emptyEdt_error_hour));

        }else if (chosenChair == 0){

            chairLayout.setError(getString(R.string.table_emptyEdt_error_chair));

        }else {

            setupReservationModel();
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.reservation_bundle), reservation);
            tableFragment.setArguments(args);
            if (getActivity()!=null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.your_placeholder, tableFragment, "table");
                ft.addToBackStack("Table");
                ft.commit();
            }
        }
    }


}
