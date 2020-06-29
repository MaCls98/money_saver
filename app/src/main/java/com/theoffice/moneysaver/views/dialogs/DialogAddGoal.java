package com.theoffice.moneysaver.views.dialogs;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyFileUtils;
import com.theoffice.moneysaver.utils.MyDatePicker;
import com.theoffice.moneysaver.utils.MyPermissionManager;
import com.theoffice.moneysaver.utils.MyToast;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;
import com.theoffice.moneysaver.views.activities.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DialogAddGoal extends DialogFragment implements View.OnClickListener {

    private ProfileViewModel viewModel;

    private TextInputLayout tilGoalName;
    private TextInputLayout tilGoalValue;
    private TextInputLayout tilGoalDate;
    private EditText etGoalDate;
    private Button btnTakePhoto;
    private ImageButton ibDeletePhoto;
    private ImageButton ibFullscreenPhoto;
    private ImageView ivGoalPhoto;

    private String goalName;
    private int goalValue;
    private String goalDate;
    private String goalPhotoPath;

    private int isGoalComplete = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        initComponents(view);
        initToolbar(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.et_goal_date:
                MyDatePicker.showDatePicker(getChildFragmentManager(), etGoalDate);
                break;
            case R.id.btn_take_photo:
                takePhoto();
                break;
            case R.id.ib_delete_photo:
                deleteGoalPhoto();
                break;
            case R.id.ib_fullscreen_photo:
                break;
        }
    }

    private void takePhoto() {
        if (MyPermissionManager.checkPermission(getActivity(), Manifest.permission.CAMERA)){
            goalPhotoPath = MyFileUtils.takeGoalPhoto(getActivity());
        }
    }

    private void addNewGoal() {
        setGoalValues(AppConstants.GOAL_NAME);
        setGoalValues(AppConstants.GOAL_VALUE);
        setGoalValues(AppConstants.GOAL_DATE);
        setGoalValues(AppConstants.GOAL_PHOTO);

        if (isGoalComplete == 4){
            Goal newGoal = new Goal(
                    "",
                    goalName,
                    goalValue,
                    0,
                    goalDate,
                    goalPhotoPath,
                    "NEW",
                    new String[]{},
                    0
            );
            try {
                uploadNewGoal(newGoal);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        isGoalComplete = 0;
    }

    private void uploadNewGoal(Goal newGoal) throws JSONException {
        User user = ApplicationMoneySaver.getMainUser();
        JSONObject goalObject = new JSONObject();
        goalObject.put("userId", user.getUserId());
        goalObject.put("goal", new JSONObject()
            .put("description", newGoal.getGoalName())
            .put("start_date", newGoal.getGoalDate())
            .put("cost", newGoal.getGoalCost())
            .put("image", getUploadPhotoUrl()));

        RequestBody body = RequestBody.create(String.valueOf(goalObject), AppConstants.JSON);
        Request request = new Request.Builder()
                .url(AppConstants.BASE_URL + AppConstants.ADD_GOAL_URL)
                .post(body)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                viewModel.updateGoalsList();
                ((MainActivity)getActivity()).changeFragment(AppConstants.MY_PROFILE);
                dismiss();
            }
        });
    }

    private String getUploadPhotoUrl() {
        //TODO Cargar imagen a cloudinary y obtener URL
        return "https://docs.mongodb.com/images/mongodb-logo.png";
    }

    private void setGoalValues(String goalConst){
        switch (goalConst){
            case AppConstants.GOAL_NAME:
                if (validateEmptyTextInputLayout(tilGoalName)){
                    goalName = Objects.requireNonNull(tilGoalName.getEditText()).getText().toString();
                    isGoalComplete++;
                }
                break;
            case AppConstants.GOAL_VALUE:
                if (validateEmptyTextInputLayout(tilGoalValue)){
                    goalValue = Integer.parseInt(Objects.requireNonNull(tilGoalValue.getEditText()).getText().toString());
                    isGoalComplete++;
                }
                break;
            case AppConstants.GOAL_DATE:
                if (validateEmptyTextInputLayout(tilGoalDate)){
                    goalDate = Objects.requireNonNull(tilGoalDate.getEditText()).getText().toString();
                    isGoalComplete++;
                }
                break;
            case AppConstants.GOAL_PHOTO:
                if (goalPhotoPath != null && goalPhotoPath.length() > 0){
                    isGoalComplete++;
                }else {
                    MyToast.showShortToast(getString(R.string.please_choose_photo_goal), getActivity());
                }
                break;
        }
    }

    private boolean validateEmptyTextInputLayout(TextInputLayout inputLayout){
        if (!Objects.requireNonNull(inputLayout.getEditText()).getText().toString().isEmpty()){
            inputLayout.setErrorEnabled(false);
            return true;
        }else {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError(getString(R.string.please_complete_empty_input));
            return false;
        }
    }

    private void initComponents(View view) {
        tilGoalName = view.findViewById(R.id.til_goal_name);
        tilGoalValue = view.findViewById(R.id.til_goal_value);
        tilGoalDate = view.findViewById(R.id.til_goal_date);
        //Objects.requireNonNull(tilGoalValue.getEditText()).addTextChangedListener(new MoneyTextWatcher(tilGoalValue.getEditText()));
        etGoalDate = view.findViewById(R.id.et_goal_date);
        etGoalDate.setText(MyDatePicker.convertDate(Calendar.getInstance().getTimeInMillis()));
        etGoalDate.setOnClickListener(this);
        btnTakePhoto = view.findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener(this);
        ibDeletePhoto = view.findViewById(R.id.ib_delete_photo);
        ibDeletePhoto.setOnClickListener(this);
        ibFullscreenPhoto = view.findViewById(R.id.ib_fullscreen_photo);
        ibFullscreenPhoto.setOnClickListener(this);
        ivGoalPhoto = view.findViewById(R.id.iv_goal_photo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants
                .REQUEST_IMAGE_CAPTURE) {
            showGoalPhoto();
        }
    }

    private void showGoalPhoto() {
        Log.d("PHOTO", goalPhotoPath);
        btnTakePhoto.setVisibility(View.GONE);
        ivGoalPhoto.setVisibility(View.VISIBLE);
        ivGoalPhoto.setImageURI(Uri.fromFile(new File(goalPhotoPath)));
        ibDeletePhoto.setVisibility(View.VISIBLE);
        ibFullscreenPhoto.setVisibility(View.VISIBLE);
    }

    private void deleteGoalPhoto(){
        btnTakePhoto.setVisibility(View.VISIBLE);
        ivGoalPhoto.setImageBitmap(null);
        ivGoalPhoto.setVisibility(View.GONE);
        ibDeletePhoto.setVisibility(View.GONE);
        ibFullscreenPhoto.setVisibility(View.GONE);
        MyFileUtils.deleteFile(goalPhotoPath);
        goalPhotoPath = "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (String permission:
             permissions) {
            if (Manifest.permission.CAMERA.equals(permission)) {
                takePhoto();
            }
        }
    }

    private void initToolbar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_add_goal);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addNewGoal();
                return true;
            }
        });
        toolbar.setTitle(R.string.add_your_goal);
        toolbar.inflateMenu(R.menu.dialog_goal_menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_goal, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setWindowAnimations(R.style.AppTheme_Slide);
    }
}
