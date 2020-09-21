package com.example.sanjay.erp.newChatScreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanjay.erp.R;

import java.util.Objects;

public class ActivityFragment extends Fragment {
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.user_chat_activity, container, false);
        FragmentTransaction fragmentTransaction2;


        fragmentTransaction2=Objects.requireNonNull(getActivity()). getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.notificationFragment, new notificationFragment());
        fragmentTransaction2.commit();
        loadAnotherFragment();
        return v;
    }
    void loadAnotherFragment(){
        FragmentTransaction fragmentTransaction2;


        fragmentTransaction2=Objects.requireNonNull(getActivity()). getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.requestFragment, new NewRequests());
        fragmentTransaction2.commit();
    }
}
