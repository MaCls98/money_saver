package com.theoffice.moneysaver.utils;

import com.theoffice.moneysaver.data.model.Contribution;
import com.theoffice.moneysaver.data.model.Goal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MyTestHelper {

    public static ArrayList<Goal> getGoalList(){
        ArrayList<Goal> goals = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            Goal goal = new Goal(
                    1,
                    "Nombre prueba" + i,
                    "$5.000",
                    MyDatePicker.convertDate(Calendar.getInstance(Locale.getDefault()).getTimeInMillis()),
                    "/storage/emulated/0/Android/data/com.theoffice.moneysaver/files/Pictures/JPEG_20200625_225120_8722133692996310947.jpg",
                    "NEW",
                    10,
                    new ArrayList<Contribution>()
            );
            goals.add(goal);
        }
        return goals;
    }

}
