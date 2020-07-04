package com.theoffice.moneysaver.views.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Goal;
import com.theoffice.moneysaver.data.model.Product;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyDatePicker;
import com.theoffice.moneysaver.utils.MyToast;
import com.theoffice.moneysaver.viewmodels.ProfileViewModel;
import com.theoffice.moneysaver.views.activities.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DialogProduct extends DialogFragment {

    private Product product;
    private ProfileViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        product = (Product) bundle.getSerializable("product");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_produt, container, false);

        TextView tvProductName = v.findViewById(R.id.tv_product_name);
        tvProductName.setText(product.getProductName());
        ImageView ivProductImage = v.findViewById(R.id.iv_product_image);
        Glide.with(getContext())
                .load(product.getProductPhoto())
                .placeholder(R.drawable.huawei_icon)
                .into(ivProductImage);

        TextView tvProductValue = v.findViewById(R.id.tv_product_value);
        tvProductValue.setText("$ " + product.getProductValue());

        Button btnAddProduct = v.findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    uploadNewGoal(new Goal(
                            "",
                            product.getProductName(),
                            product.getProductValue(),
                            0,
                            MyDatePicker.convertDate(Calendar.getInstance().getTimeInMillis()),
                            product.getProductPhoto(),
                            "NEW",
                            new String[]{},
                            0)
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    private void uploadNewGoal(final Goal newGoal) throws JSONException {
        User user = ApplicationMoneySaver.getMainUser();
        JSONObject goalObject = new JSONObject();
        goalObject.put("userId", user.getUserId());
        goalObject.put("goal", new JSONObject()
                .put("description", newGoal.getGoalName())
                .put("start_date", newGoal.getGoalDate())
                .put("cost", newGoal.getGoalCost())
                .put("image", newGoal.getGoalPhotoPath()))
                .put("goal_type", AppConstants.GOAL_TYPE_HUAWEI);

        RequestBody body = RequestBody.create(String.valueOf(goalObject), AppConstants.JSON);
        Request request = new Request.Builder()
                .url(AppConstants.BASE_URL + AppConstants.ADD_GOAL_URL)
                .post(body)
                .build();
        ApplicationMoneySaver.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showLongToast("Ocurrio un error, por favor vuele a intentarlo", getActivity());
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                viewModel.updateGoalsList();
                //MyFileUtils.deleteFile(newGoal.getGoalPhotoPath());
                ((MainActivity)getActivity()).changeFragment(AppConstants.MY_PROFILE);
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Objects.requireNonNull(getDialog().getWindow()).setWindowAnimations(R.style.AppTheme_Slide);
    }
}
