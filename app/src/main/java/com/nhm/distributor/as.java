package com.nhm.distributor;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class as extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a);
String[] ss = new String[]{};
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog= new DatePickerDialog(as.this, R.style.monthDialogStyle, new DatePickerDialog.OnDateSetListener() {
            @Override    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        }, mYear, mMonth, mDay);

        dialog.getDatePicker().findViewById(getResources().getIdentifier("year","id","android")).setVisibility(View.GONE);
        dialog.getDatePicker().findViewById(getResources().getIdentifier("month","id","android")).setVisibility(View.GONE);
        dialog.show();

//        Calendar cal = Calendar.getInstance();
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH);
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//        DatePickerDialog mDatePicker = new DatePickerDialog(as.this, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int selectedyear, int selectedmonth, int selectedday) {
//                // Stuff
//            }
//        }, year, month, day) {
//
//            final int month = getContext().getResources().getIdentifier("android:id/month", null, null);
//            final String[] monthNumbers = new String[]{ "01","02","03","04","05","06","07","08","09","10","11","12"};
//
//            @Override
//            public void onDateChanged(@NonNull DatePicker view, int y, int m, int d) {
//                super.onDateChanged(view, y, m, d);
//                // Since DatePickerCalendarDelegate updates the month spinner too, we need to change months as numbers here also
//                if(month != 0){
//                    NumberPicker monthPicker = findViewById(month);
//                    if(monthPicker != null){
//                        monthPicker.setDisplayedValues(monthNumbers);
//                    }
//                }
//            }
//
//            @Override
//            protected void onCreate(Bundle savedInstanceState)
//            {
//                super.onCreate(savedInstanceState);
//                // Hide day spinner
//                int day = getContext().getResources().getIdentifier("android:id/day", null, null);
//                if(day != 0){
//                    NumberPicker dayPicker = findViewById(day);
//                    if(dayPicker != null){
//                        dayPicker.setVisibility(View.GONE);
//                    }
//                }
//                // Show months as Numbers
//                if(month != 0){
//                    NumberPicker monthPicker = findViewById(month);
//                    if(monthPicker != null){
//                        monthPicker.setDisplayedValues(monthNumbers);
//                    }
//                }
//            }
//        };
//        mDatePicker.setTitle("Select Date");
//        mDatePicker.show();

    }





}
