package com.example.ujchatapp.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.chaos.view.PinView;
import com.example.ujchatapp.Constants.AllConstants;
import com.example.ujchatapp.R;
import com.example.ujchatapp.UserModel;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;



public class VerifyNumber extends Fragment {


    Button verifyBtn;
    PinView pinCodeTv;
    RelativeLayout progressLayout;
    CamomileSpinner progress;

    private String OTP, pin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    public VerifyNumber() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_number, container, false);

        verifyBtn = view.findViewById(R.id.verify_btn);
        progress = view.findViewById(R.id.progress);
        progressLayout = view.findViewById(R.id.progress_layout);
        pinCodeTv = view.findViewById(R.id.code_text);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Bundle bundle = getArguments();


        if (bundle != null) {
            OTP = bundle.getString(AllConstants.VERIFICATION_CODE);
        }

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPin();
                if (checkPin()) {
                    progressLayout.setVisibility(View.VISIBLE);
                    progress.start();

                    verifyPin(pin);
                }
            }
        });

        return view;
    }


    private boolean checkPin() {

        pin = pinCodeTv.getText().toString();
        if (TextUtils.isEmpty(pin)) {
            pinCodeTv.setError("Enter the pin");
            return false;
        } else if (pin.length() < 6) {
            pinCodeTv.setError("Enter valid pin");
            return false;
        } else {
            pinCodeTv.setError(null);
            return true;
        }
    }

    private void verifyPin(String pin) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, pin);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {


        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(data -> {

                        String token = data.getResult().getToken();
                        UserModel userModel = new UserModel("", "", "", firebaseAuth.getCurrentUser().getPhoneNumber(),
                                firebaseAuth.getUid(), "online", "false", token);
                        databaseReference.child(firebaseAuth.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    getFragmentManager().beginTransaction().replace(R.id.container, new CompleteUserData()).commit();
                                    progressLayout.setVisibility(View.GONE);
                                    progress.stop();

                                } else
                                    Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                } else
                    Toast.makeText(getContext(), "" + task.getResult(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
