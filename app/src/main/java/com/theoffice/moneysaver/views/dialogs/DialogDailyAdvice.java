package com.theoffice.moneysaver.views.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.theoffice.moneysaver.R;

import java.util.ArrayList;

public class DialogDailyAdvice extends DialogFragment {

    private TextView tvAdvice;
    private ImageButton ibRefresh;
    private ArrayList<String> advices;
    int i = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_daily_advice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvAdvice = view.findViewById(R.id.tv_advice);
        advices = new ArrayList<>();
        advices.add(getString(R.string.consejo_1));
        advices.add(getString(R.string.consejo_2));
        advices.add(getString(R.string.consejo_3));
        advices.add(getString(R.string.consejo_4));

        tvAdvice.setText(advices.get(i));

        ibRefresh = view.findViewById(R.id.ib_refresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i < 3){
                    tvAdvice.setText(advices.get(i));
                    i++;
                }else {
                    tvAdvice.setText(advices.get(i));
                    i = 0;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
