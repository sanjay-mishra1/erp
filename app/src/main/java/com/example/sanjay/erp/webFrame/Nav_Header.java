package com.example.sanjay.erp.webFrame;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.R;

import java.util.Objects;

public class Nav_Header extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.img_layout, container, false);
        Glide.with(Objects.requireNonNull(getContext())).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(Objects.requireNonNull(this.getArguments()).getString("IMG")).
                into((ImageView) v.findViewById(R.id.imageview));

        return v;
    }
}