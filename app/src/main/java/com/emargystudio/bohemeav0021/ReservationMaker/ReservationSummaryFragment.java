package com.emargystudio.bohemeav0021.ReservationMaker;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emargystudio.bohemeav0021.helperClasses.CommonReservation;
import com.emargystudio.bohemeav0021.Model.Reservation;
import com.emargystudio.bohemeav0021.R;

public class ReservationSummaryFragment extends Fragment {

    private static final String TAG = "ReservationSummaryFragm";

    //widget
    TextView txtDate ;
    TextView txtHour ;
    TextView txtChair ;
    TextView txtTable ;
    Button preOrderBtn, homeBtn;
    Reservation reservation;
    ProgressBar progressBar;

    public ReservationSummaryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this datafragment
        return inflater.inflate(R.layout.fragment_reservation_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {



         txtDate = view.findViewById(R.id.date_txt);
         txtHour = view.findViewById(R.id.hour_txt);
         txtChair = view.findViewById(R.id.chair_txt);
         txtTable = view.findViewById(R.id.table_txt);
         preOrderBtn = view.findViewById(R.id.preOrderBtn);
         progressBar = view.findViewById(R.id.progressBar_cyclic);
         homeBtn = view.findViewById(R.id.home);




             try {
                 reservation = getReservationFromBundle();
                 txtDate.setText("DATE : " + reservation.getYear() + "/" + reservation.getMonth() + "/" + reservation.getDay());
                 txtHour.setText("START AT : " + CommonReservation.changeHourFormat(reservation.getStartHour()));
                 txtChair.setText("RESERVATION FOR : " + reservation.getChairNumber());
                 txtTable.setText("YOUR TABLE NUMBER : " + reservation.getTable_id());
             } catch (NullPointerException e) {
                 Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();

         }

         // send reservation form(final step)


        //listener
        preOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }

    private Reservation getReservationFromBundle(){

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.reservation_bundle));
        }else{
            return null;
        }
    }



}
