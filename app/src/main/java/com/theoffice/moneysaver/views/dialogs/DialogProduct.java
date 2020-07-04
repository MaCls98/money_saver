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

import com.bumptech.glide.Glide;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.Product;

import java.util.Objects;

public class DialogProduct extends DialogFragment {

    private Product product;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        product = (Product) bundle.getSerializable("product");
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

            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Objects.requireNonNull(getDialog().getWindow()).setWindowAnimations(R.style.AppTheme_Slide);
    }
}
