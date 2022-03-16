package com.example.ujchatapp.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.chaos.view.PinView;
import com.example.ujchatapp.Activity.DashBoard;
import com.example.ujchatapp.Constants.AllConstants;
import com.example.ujchatapp.R;
import com.example.ujchatapp.UserModel;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;


public class VerifyNumber extends Fragment {

    Button verifyBtn;
    PinView pinCodeTv;
    RelativeLayout progressLayout;
    CamomileSpinner progress;

    private String OTP, pin, phoneNumber;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    public VerifyNumber() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();


        if (bundle != null) {
            phoneNumber = bundle.getString(AllConstants.VERIFICATION_CODE);
        }




        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("COmpleted", "onVerificationCompleted:" + credential);

                pinCodeTv.setText(credential.getSmsCode());
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("failed", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("codesent", "onCodeSent:" + verificationId);
                Toast.makeText(getContext(), getResources().getString(R.string.the_code_has_been_sent), Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later
                OTP = verificationId;
                // ...
            }
        };


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);

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

                        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.child("name").exists()){
                                    Toast.makeText(getContext(), "user existed", Toast.LENGTH_SHORT).show();
                                    getFragmentManager().beginTransaction().replace(R.id.container, new AccountExisted()).commit();
                                    progressLayout.setVisibility(View.GONE);
                                    progress.stop();
                                }else{
                                    //create new user if not existed
                                    Bundle bundle = new Bundle();
                                    UserModel userModel = new UserModel("", "", "", firebaseAuth.getCurrentUser().getPhoneNumber(),
                                            firebaseAuth.getUid(), "online", "false", token, "", "", "", "", "", AllConstants.USER_TYPE_STUDENT);

                                    bundle.putSerializable("user", userModel);
//                                    databaseReference.child(firebaseAuth.getUid()).setValue(userModel);
                                    CompleteUserData fragment = new CompleteUserData();
                                    fragment.setArguments(bundle);
                                    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                                    progressLayout.setVisibility(View.GONE);
                                    progress.stop();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    });

                } else
                    Toast.makeText(getContext(), "" + task.getResult(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
