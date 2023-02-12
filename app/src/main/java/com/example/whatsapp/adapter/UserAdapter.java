package com.example.whatsapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.activity.ChatActivity;
import com.example.whatsapp.activity.HomeActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.modal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter <UserAdapter.ViewHolder>{

    HomeActivity homeActivity;
    ArrayList<User> usersArraylist;
    public UserAdapter(HomeActivity homeActivity, ArrayList<User> usersArraylist) {
        this.homeActivity=homeActivity;
        this.usersArraylist=usersArraylist;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
User newuser=usersArraylist.get(position);
    holder.username.setText(newuser.getName());
    holder.userstatus.setText(newuser.getStatus());
    Picasso.get().load(newuser.getImageURI()).into(holder.userprofile);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(homeActivity, ChatActivity.class);
                i.putExtra("name",newuser.getName());
                i.putExtra("ReciecerImage",newuser.getImageURI());
                i.putExtra("uid",newuser.getUid());
                homeActivity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArraylist.size();
    }



    public  class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userprofile;
        private TextView username,userstatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userprofile=itemView.findViewById(R.id.image_user);
            username=itemView.findViewById(R.id.user_name);
            userstatus=itemView.findViewById(R.id.user_status);
        }
    }
}
