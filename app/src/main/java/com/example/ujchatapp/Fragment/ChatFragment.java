package com.example.ujchatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ujchatapp.MessageActivity;
import com.example.ujchatapp.R;
import com.example.ujchatapp.Utils.Util;
import com.example.ujchatapp.ChatListModel;
import com.example.ujchatapp.ChatModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    RecyclerView mChatsList;
    private Util util;
    private FirebaseRecyclerAdapter<ChatListModel, ViewHolder> firebaseRecyclerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mChatsList = view.findViewById(R.id.recyclerViewChat);

        util = new Util();

        readChat();

        return view;
    }

    private void readChat() {
        Query query = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(util.getUID());

        FirebaseRecyclerOptions<ChatListModel> options = new FirebaseRecyclerOptions.Builder<ChatListModel>()
                .setQuery(query, ChatListModel.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatListModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i, @NonNull final ChatListModel chatModel) {

                String userID = chatModel.getMember();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Date time = null;
                            String name = dataSnapshot.child("name").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();
                            String online = dataSnapshot.child("online").getValue().toString();
                            Calendar calendar = Calendar.getInstance();
                            try {
                                time = Util.sdf().parse(chatModel.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            assert time != null;
                            calendar.setTime(time);
                            String date = Util.getTimeAgo(calendar.getTimeInMillis());
                            ChatModel chat = new ChatModel(chatModel.getChatListID(), name, chatModel.getLastMessage()
                                    , image, date, online);

                            viewHolder.name.setText(chat.getName());
                            viewHolder.date.setText(chat.getDate());
                            viewHolder.status.setText(chat.getLastMessage());
                            if(chat.getOnline().equals("online")){
                                viewHolder.statusImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_online));
                            }else {
                                viewHolder.statusImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_offline));
                            }

                            viewHolder.itemView.setOnClickListener(view -> {
                                Intent intent = new Intent(getContext(), MessageActivity.class);
                                intent.putExtra("hisID", userID);
                                intent.putExtra("hisImage", image);
                                intent.putExtra("chatID", chatModel.getChatListID());
                                startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View listItem = layoutInflater.inflate(R.layout.chat_item_layout, parent, false);

                return new ViewHolder(listItem);
            }
        };

        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mChatsList.setHasFixedSize(false);
        mChatsList.setAdapter(firebaseRecyclerAdapter);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, status, date;
        ImageView statusImg;
        CircleImageView profileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.txtChatName);
            this.status = itemView.findViewById(R.id.txtChatStatus);
            this.date = itemView.findViewById(R.id.txtChatDate);
            this.statusImg = itemView.findViewById(R.id.status_image);
            this.profileImg = itemView.findViewById(R.id.imgChatProfile);
        }
    }

    @Override
    public void onResume() {
        firebaseRecyclerAdapter.startListening();
        super.onResume();
    }

    @Override
    public void onPause() {
        firebaseRecyclerAdapter.stopListening();
        super.onPause();
    }

}