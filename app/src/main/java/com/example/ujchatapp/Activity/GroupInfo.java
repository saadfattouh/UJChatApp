package com.example.ujchatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ujchatapp.Adapter.ContactAdapter;
import com.example.ujchatapp.GroupMessageActivity;
import com.example.ujchatapp.R;
import com.example.ujchatapp.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfo extends AppCompatActivity {

    TextView mTitle, mTutorName, mSection, mCourseCode;
    CircleImageView mGroupImage;

    RecyclerView mStudentsList;
    private ArrayList<UserModel> groupStudents;
    private ContactAdapter contactAdapter;

    String groupId;

    DatabaseReference databaseReference;
    DatabaseReference usersIdsDataReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        mTitle = findViewById(R.id.title);
        mTutorName = findViewById(R.id.tutor_name);
        mSection = findViewById(R.id.section);
        mCourseCode = findViewById(R.id.course_code);
        mGroupImage = findViewById(R.id.group_image);
        mStudentsList = findViewById(R.id.students_list);


        Intent sender = getIntent();
        if(sender != null){
            groupId = sender.getStringExtra("chat_id");
        }


        getGroupData();





    }

    private void getGroupData() {
        groupStudents = new ArrayList<UserModel>();
        contactAdapter = new ContactAdapter(this, groupStudents);
        mStudentsList.setAdapter(contactAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String image = snapshot.child("info").child("image").getValue().toString();
                    String tutorName = snapshot.child("info").child("instructorName").getValue().toString();
                    String section = snapshot.child("info").child("section").getValue().toString();
                    String course_code = snapshot.child("info").child("courseCode").getValue().toString();
                    String title = snapshot.child("info").child("title").getValue().toString();

                    Glide.with(GroupInfo.this).load(image).into(mGroupImage);
                    mTutorName.setText(tutorName);
                    mTitle.setText(title);
                    mSection.setText(section);
                    mCourseCode.setText(course_code);

                    for (DataSnapshot userId: snapshot.child("info").child("users").getChildren()){

                        Log.e("user in group : ", userId.getValue().toString());
                        usersIdsDataReference = FirebaseDatabase.getInstance().getReference("Users").child(userId.getValue().toString());
                        usersIdsDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserModel userModel = snapshot.getValue(UserModel.class);
                                groupStudents.add(userModel);
                                contactAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}