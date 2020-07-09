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

    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    public static String getTimeAgo(long duration) {
        long cur_time = (Calendar.getInstance().getTimeInMillis()) / 1000;
        long time_elapsed = cur_time - (duration / 1000);
        long seconds = time_elapsed;
        int minutes = Math.round(time_elapsed / 60);
        int hours = Math.round(time_elapsed / 3600);
        int days = Math.round(time_elapsed / 86400);
        int weeks = Math.round(time_elapsed / 604800);
        int months = Math.round(time_elapsed / 2600640);
        int years = Math.round(time_elapsed / 31207680);

        // Seconds
        if (seconds <= 60) {
            return "Justo ahora";
        }
        //Minutes
        else if (minutes <= 60) {
            if (minutes == 1) {
                return "Hace 1 minuto";
            } else {
                return "Hace " + minutes + "  minutos";
            }
        }
        //Hours
        else if (hours <= 24) {
            if (hours == 1) {
                return "Hace 1 hora";
            } else {
                return "Hace " + hours + " horas";
            }
        }
        //Days
        else if (days <= 7) {
            if (days == 1) {
                return "Ayer";
            } else {
                return "Hace " + days + " dias";
            }
        }
        //Weeks
        else if (weeks <= 4.3) {
            if (weeks == 1) {
                return "Hace una semana";
            } else {
                return "Hace " + weeks + " semanas";
            }
        }
        //Months
        else if (months <= 12) {
            if (months == 1) {
                return "a month ago";
            } else {
                return months + " months ago";
            }
        }
        //Years
        else {
            if (years == 1) {
                return "one year ago";
            } else {
                return years + " years ago";
            }
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
