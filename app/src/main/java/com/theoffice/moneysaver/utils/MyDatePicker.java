package com.theoffice.moneysaver.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.theoffice.moneysaver.R;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDatePicker {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static String getTimeAgo(long time, Context c) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Hace un momento";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Hace un minuto";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "Hace " +  diff / MINUTE_MILLIS + " minutos";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Hace una hora";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "Hace " + diff / HOUR_MILLIS + " horas";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Ayer";
        } else {
            return "Hace " + diff / DAY_MILLIS + " " + "dias";
        }
    }


    public static void showDatePickr(Context context, final EditText editText){
        DatePickerDialog pickerDialog = new DatePickerDialog(context);
        pickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                editText.setText(day + "-" + (month+1) + "-" + year);
            }
        });
        pickerDialog.show();
    }

    public static String convertDate(Long selection) {
        //LocalDateTime utcDateTimeForCurrentDateTime = Instant.ofEpochMilli(now.getTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
        LocalDate localDate = Instant.ofEpochMilli(selection).atZone(ZoneId.of("UTC")).toLocalDate();
        DateTimeFormatter dTF2 = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);
        return dTF2.format(localDate);
    }

    public static String getActualDate(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.YEAR);
    }

    private static MaterialDatePicker.Builder<Long> datePicker;

    public static void showDatePicker(FragmentManager fragmentManager, final EditText editText){
        Log.d("VIEW", "Date Picker");
        datePicker = MaterialDatePicker.Builder.datePicker().setSelection(Calendar.getInstance().getTimeInMillis());
        MaterialDatePicker<Long> picker = datePicker.build();
        picker.show(fragmentManager, picker.getTag());

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Log.d("VIEW", convertDate(selection));
                editText.setText(convertDate(selection));
            }
        });
    }
}
