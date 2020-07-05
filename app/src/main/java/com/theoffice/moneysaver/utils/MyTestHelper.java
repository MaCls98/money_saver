package com.theoffice.moneysaver.utils;

import com.theoffice.moneysaver.data.model.Contribution;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MyTestHelper {

    public static User getDefaultUser(){
        return new User("5ef00cbb4de149bdd14c0c5d", "Alexander", "https://docs.mongodb.com/images/mongodb-logo.png");
    }

    public static ArrayList<Goal> getGoalList(){
        ArrayList<Goal> goals = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            Goal goal = new Goal(
                    "",
                    "Nombre prueba" + i,
                    0,
                    5000,
                    MyDatePicker.convertDate(Calendar.getInstance(Locale.getDefault()).getTimeInMillis()),
                    "/storage/emulated/0/Android/data/com.theoffice.moneysaver/files/Pictures/JPEG_20200625_225120_8722133692996310947.jpg",
                    "NEW",
                    new ArrayList<String>(),
                    2
            );
            goals.add(goal);
        }
        return goals;
    }

}
