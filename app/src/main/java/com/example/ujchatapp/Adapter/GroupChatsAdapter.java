package com.example.ujchatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ujchatapp.Activity.GroupInfo;
import com.example.ujchatapp.GroupInfoModel;
import com.example.ujchatapp.GroupMessageActivity;
import com.example.ujchatapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatsAdapter extends RecyclerView.Adapter<GroupChatsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<GroupInfoModel> arrayList, filterArrayList;


    public GroupChatsAdapter(Context context, ArrayList<GroupInfoModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_group, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final GroupInfoModel group = arrayList.get(position);

        holder.name.setText(group.getTitle());

        Glide.with(context).load(group.getImage()).timeout(6000).into(holder.image);

        holder.time.setText(group.getTime());

        holder.lastMessage.setText(group.getLastMessage());

        holder.lastMessageSender.setText(group.getLastMessageSender());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupMessageActivity.class);
                intent.putExtra("chat_id", group.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, lastMessageSender, lastMessage;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.group_name);
            this.time = itemView.findViewById(R.id.msg_time);
            this.lastMessage = itemView.findViewById(R.id.last_msg);
            this.lastMessageSender = itemView.findViewById(R.id.last_msg_sender);
            this.image = itemView.findViewById(R.id.img);
        }
    }
}
