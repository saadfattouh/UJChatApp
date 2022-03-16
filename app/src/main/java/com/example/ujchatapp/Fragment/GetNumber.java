package com.example.ujchatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import com.example.ujchatapp.Activity.DashBoard;
import com.example.ujchatapp.Constants.AllConstants;
import com.example.ujchatapp.R;
import com.example.ujchatapp.UserModel;
import com.example.ujchatapp.databinding.FragmentGetNumberBinding;
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
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;



public class GetNumber extends Fragment {

    private String number;

    EditText phoneEt;
    Button verifyBtn;
    RelativeLayout progressLayout;
    CamomileSpinner progress;
    CountryCodePicker countryChooser;

//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference databaseReference;

    public GetNumber() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_number, container, false);

//        firebaseAuth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        phoneEt = view.findViewById(R.id.phone_number);
        verifyBtn = view.findViewById(R.id.verify_btn);
        progressLayout = view.findViewById(R.id.progress_layout);
        progress = view.findViewById(R.id.progress);
        countryChooser = view.findViewById(R.id.country_code_chooser);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNumber();
                if (checkNumber()) {
                    String phoneNumber = countryChooser.getSelectedCountryCodeWithPlus() + number;

                    VerifyNumber fragment = new VerifyNumber();
                    Bundle bundle = new Bundle();
                    bundle.putString(AllConstants.VERIFICATION_CODE, phoneNumber);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment)
                            .commit();

//                    sendOTP(phoneNumber);
                }
            }
        });
        return view;
    }

    private boolean checkNumber() {
        number = phoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            phoneEt.setError("Enter number");
            return false;
        } else if (number.length() < 10) {
            phoneEt.setError("Enter valid number");
            return false;
        } else {
            phoneEt.setError(null);

            return true;
        }
    }

//    private void sendOTP(String phoneNumber) {
//        progressLayout.setVisibility(View.VISIBLE);
//        progress.start();
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                getActivity(),               // Activity (for callback binding)
//                mCallbacks);
//    }
//
//    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
//            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//
//
//                @Override
//                public void onVerificationCompleted(PhoneAuthCredential credential) {
//                    Toast.makeText(getContext(), "doneeeee", Toast.LENGTH_SHORT).show();
//
////                    signInWithPhoneAuthCredential(credential);
////                    progressLayout.setVisibility(View.GONE);
////                    progress.stop();
//
//
//                }
//
//                @Override
//                public void onVerificationFailed(FirebaseException e) {
//
//                    if (e instanceof FirebaseAuthInvalidCredentialsException){
//                        Log.e("taaaaaaaaag:::", e.getMessage());
//                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                    else if (e instanceof FirebaseTooManyRequestsException)
//                        Toast.makeText(getContext(), "The SMS quota for the project has been exceeded ", Toast.LENGTH_LONG).show();
//
//
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                    progressLayout.setVisibility(View.GONE);
//                    progress.stop();
//
//
//                }
//
//
//                @Override
//                public void onCodeSent(@NonNull String s,
//                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                    Toast.makeText(getContext(), getResources().getString(R.string.the_code_has_been_sent), Toast.LENGTH_LONG).show();
//
//                    Fragment fragment = new VerifyNumber();
//                    Bundle bundle = new Bundle();
//                    bundle.putString(AllConstants.VERIFICATION_CODE, s);
//                    fragment.setArguments(bundle);
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.container, fragment)
//                            .commit();
//                    progressLayout.setVisibility(View.GONE);
//                    progress.stop();
//
//
//                }
//            };


//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//
//
//        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//
//                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(data -> {
//
//                        String token = data.getResult().getToken();
//
//                        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                //user is already existed!
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//                    });
//
//                } else
//                    Toast.makeText(getContext(), "" + task.getResult(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
