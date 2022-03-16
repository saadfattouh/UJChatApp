package com.example.ujchatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ujchatapp.Activity.DashBoard;
import com.example.ujchatapp.Activity.IntroActivity;
import com.example.ujchatapp.Activity.MainActivity;
import com.example.ujchatapp.R;


public class AccountExisted extends Fragment {


    public AccountExisted() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_existed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), DashBoard.class);
                startActivity(intent);
                getActivity().finish();
            }
        }, 2000);


    }
}