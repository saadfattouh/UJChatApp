package com.example.ujchatapp.Fragment;

import android.bluetooth.le.AdvertiseData;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ujchatapp.Activity.CreateGroupActivity;
import com.example.ujchatapp.Adapter.GroupChatsAdapter;
import com.example.ujchatapp.ChatListModel;
import com.example.ujchatapp.ChatModel;
import com.example.ujchatapp.GroupChatModel;
import com.example.ujchatapp.GroupInfoModel;
import com.example.ujchatapp.MessageActivity;
import com.example.ujchatapp.R;
import com.example.ujchatapp.Utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupsFragment extends Fragment {

    RecyclerView mChatsList;
    private Util util;
    ArrayList<GroupInfoModel> groups;
    private GroupChatsAdapter mAdapter;

    FloatingActionButton addBtn;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        mChatsList = view.findViewById(R.id.recyclerViewChat);
        addBtn = view.findViewById(R.id.floating_action_button);

        addBtn.setOnClickListener(v -> {
            addNewGroup();
        });

        util = new Util();

        readChat();

        return view;
    }

    private void addNewGroup() {
        Intent createGroup = new Intent(getContext(), CreateGroupActivity.class);
        startActivity(createGroup);
    }

    private void readChat() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groups = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    GroupInfoModel info = new GroupInfoModel();

                    String title = dataSnapshot.child("info").child("title").getValue().toString();
                    String image = dataSnapshot.child("info").child("image").getValue().toString();
                    String date = dataSnapshot.child("info").child("courseCode").getValue().toString();
                    String status = dataSnapshot.child("info").child("section").getValue().toString();
                    String id = dataSnapshot.child("info").child("id").getValue().toString();
                    String time = dataSnapshot.child("info").child("time").getValue().toString();
                    String lastMessage = dataSnapshot.child("info").child("lastMessage").getValue().toString();
                    String lastMessageSender = dataSnapshot.child("info").child("lastMessageSender").getValue().toString();

                    if(lastMessage.isEmpty()){
                        info.setLastMessage("nothing");
                        info.setLastMessageSender("nobody: ");
                    }else {
                        info.setLastMessage(lastMessage);
                        info.setLastMessageSender(lastMessageSender);
                    }



                    info.setTitle(title);
                    info.setCourseCode(date);
                    info.setSection(status);
                    info.setId(id);
                    info.setImage(image);
                    info.setTime(time);

                    groups.add(info);
                }

                mAdapter = new GroupChatsAdapter(getContext(), groups);
                mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));
                mChatsList.setHasFixedSize(false);
                mChatsList.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}