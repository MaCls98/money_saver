package com.theoffice.moneysaver.utils;

import android.util.Log;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

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

    public static String convertDate(Long selection) {
        //LocalDateTime utcDateTimeForCurrentDateTime = Instant.ofEpochMilli(now.getTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
        LocalDate localDate = Instant.ofEpochMilli(selection).atZone(ZoneId.of("UTC")).toLocalDate();
        DateTimeFormatter dTF2 = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);
        return dTF2.format(localDate);
    }
}
