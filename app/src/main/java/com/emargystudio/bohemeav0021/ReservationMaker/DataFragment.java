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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.emargystudio.bohemeav0021.helperClasses.CommonReservation;
import com.emargystudio.bohemeav0021.HomeActivity;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

import static com.emargystudio.bohemeav0021.helperClasses.CommonReservation.hideSoftKeyboard;


public class DataFragment extends Fragment {

    private static final String TAG = "DataFragment";

    TextView txtData;
    ImageView backBtn;
    EditText edtDate , edtHour , edtChairs;
    FloatingActionButton nextFAB;
    TextInputLayout dataLayout,hourLayout,chairLayout;
    ConstraintLayout constraintLayoutl;


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
        backBtn = view.findViewById(R.id.backBtn);
        edtDate = view.findViewById(R.id.edtDate);
        edtHour = view.findViewById(R.id.edtHour);
        edtChairs = view.findViewById(R.id.edtChairs);
        txtData = view.findViewById(R.id.textView);
        nextFAB = view.findViewById(R.id.nextFAB);
        dataLayout = view.findViewById(R.id.dateLayout);
        hourLayout = view.findViewById(R.id.hourLayout);
        chairLayout = view.findViewById(R.id.chairLayout);
        constraintLayoutl = view.findViewById(R.id.constraint1);

        //var
        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay   = calendar.get(Calendar.DAY_OF_MONTH);
        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);
        tableFragment = new TableFragment();

        Log.d(TAG, "year: "+chosenYear);



        //change fonts
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NABILA.TTF");
        txtData.setTypeface(face);


        //initEditTexts();



        // listener
        backBtn.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        setupReservationModel();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.reservation_bundle), reservation);
        outState.putBundle("args",args);
        Log.d(TAG, "onSaveInstanceState: "+outState.toString());
        super.onSaveInstanceState(outState);
    }

    public void datePicker(int currentYear, int currentMonth, int currentDay){

        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
            Log.d(TAG, "onViewCreated: "+savedInstanceState.toString());
            Bundle bundle = savedInstanceState.getBundle("args");
            if (bundle != null) {
                reservation = bundle.getParcelable(getString(R.string.reservation_bundle));
                chosenYear = reservation.getYear();
                chosenMonth = reservation.getMonth();
                chosenDay = reservation.getDay();
                chosenStartHour = reservation.getStartHour();
                chosenEndHour = reservation.getEnd_hour();
                chosenChair = reservation.getChairNumber();
            }

        }
        super.onActivityCreated(savedInstanceState);
    }

    public void nextFragment(){
        if (chosenYear == 0 || chosenMonth == 0 || chosenDay == 0) {
            dataLayout.setError("Pick a date before");


        }else if (chosenStartHour == 0 && chosenEndHour == 0 ) {

            hourLayout.setError("Choose an hour first");

        }else if (chosenChair == 0){

            chairLayout.setError("Fill this field");

        }else {

            setupReservationModel();
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.reservation_bundle), reservation);
            tableFragment.setArguments(args);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.your_placeholder, tableFragment,"table");
            ft.addToBackStack("Table");
            ft.commit();
        }
    }

    //hide soft keyBoard
//    public void setupUI(View view) {
//
//        // Set up touch listener for non-text box views to hide keyboard.
//        if (!(view instanceof EditText)) {
//            view.setOnTouchListener(new View.OnTouchListener() {
//                public boolean onTouch(View v, MotionEvent event) {
//                    hideSoftKeyboard(getActivity());
//                    return false;
//                }
//            });
//        }
//
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                View innerView = ((ViewGroup) view).getChildAt(i);
//                setupUI(innerView);
//            }
//        }

 //   }
}
