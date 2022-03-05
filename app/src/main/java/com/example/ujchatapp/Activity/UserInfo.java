package com.example.ujchatapp.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.ujchatapp.R;
import com.example.ujchatapp.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends AppCompatActivity {



    CircleImageView profileImage;
    TextView profileName, profileStatus, profileNumber, fName, lName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        profileImage = findViewById(R.id.imgProfile);
        profileName = findViewById(R.id.txtProfileName);
        profileNumber = findViewById(R.id.txtProfileNumber);
        profileStatus = findViewById(R.id.txtProfileStatus);
        fName = findViewById(R.id.txtProfileFName);
        lName = findViewById(R.id.txtProfileLName);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String uID = getIntent().getStringExtra("userID");

        getUserDetail(uID);
    }

    private void getUserDetail(String uID) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);

                    Glide.with(UserInfo.this)
                            .load(userModel.getImage())
                            .timeout(30000)
                            .into(profileImage);

                    profileName.setText(userModel.getName());

                    profileStatus.setText(userModel.getStatus());

                    profileNumber.setText(userModel.getNumber());

                    if (userModel.getName().contains(" ")) {
                        String[] split = userModel.getName().split(" ");
                        fName.setText(split[0]);
                        lName.setText(split[1]);
                    } else {
                        fName.setText(userModel.getName());
                        lName.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}