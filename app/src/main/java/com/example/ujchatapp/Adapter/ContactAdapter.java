package com.example.ujchatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.ujchatapp.MessageActivity;
import com.example.ujchatapp.Activity.UserInfo;
import com.example.ujchatapp.R;
import com.example.ujchatapp.UserModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<UserModel> arrayList, filterArrayList;


    public ContactAdapter(Context context, ArrayList<UserModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        filterArrayList = new ArrayList<>();
        filterArrayList.addAll(arrayList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.contact_item_layout, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final UserModel userModel = arrayList.get(position);

        holder.name.setText(userModel.getName());
        holder.status.setText(userModel.getStatus());

        Glide.with(context).load(userModel.getImage()).timeout(6000).into(holder.profileImg);


        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfo.class);
                intent.putExtra("userID", userModel.getuID());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("hisID", userModel.getuID());
                intent.putExtra("hisImage", userModel.getImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<UserModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
                filteredList.addAll(filterArrayList);
            else {
                String filter = constraint.toString().toLowerCase().trim();
                for (UserModel userModel : filterArrayList) {
                    if (userModel.getName().toLowerCase().contains(filter))
                        filteredList.add(userModel);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((Collection<? extends UserModel>) results.values);
            notifyDataSetChanged();

        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, status;
        ImageView info;
        CircleImageView profileImg;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtContactName);
            status = itemView.findViewById(R.id.txtContactStatus);
            info = itemView.findViewById(R.id.imgContactUserInfo);
            profileImg = itemView.findViewById(R.id.imgContact);
        }
    }
}
