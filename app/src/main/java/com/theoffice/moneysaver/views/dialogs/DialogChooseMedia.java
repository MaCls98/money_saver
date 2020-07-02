package com.theoffice.moneysaver.views.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.theoffice.moneysaver.R;

public class DialogChooseMedia extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_choose_media, container, false);

        final DialogAddGoal goal = (DialogAddGoal) getTargetFragment();

        Button btnCamera = v.findViewById(R.id.btn_take_photo);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goal.takePhoto();
                dismiss();
            }
        });
        Button btnGalery = v.findViewById(R.id.btn_open_galery);
        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goal.openGalery();
                dismiss();
            }
        });

        return v;
    }
}
