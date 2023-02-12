package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.UserAdapter;
import com.example.whatsapp.modal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private RecyclerView mainUserRecyclerView;

    private ImageView settingbtn;

    private UserAdapter userAdapter;
    private ImageView imgLogout;

    private FirebaseDatabase database;

    private ArrayList<User> usersArraylist;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();

        settingbtn=findViewById(R.id.settingbtn);


        imgLogout = findViewById(R.id.imglogOut);


        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdialogue();
            }
        });

        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        usersArraylist = new ArrayList<>();
        userAdapter = new UserAdapter(HomeActivity.this, usersArraylist);
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getUid().equals(auth.getCurrentUser().getUid()))
                        usersArraylist.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainUserRecyclerView.setAdapter(userAdapter);

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
    }

    public void createdialogue() {
        Dialog dialog = new Dialog(this, R.style.Dialogue);
        dialog.setContentView(R.layout.dialogue_item);
        TextView yesbtn, nobtn;
        yesbtn = dialog.findViewById(R.id.yes_logout);
        nobtn = dialog.findViewById(R.id.no_logout);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));

            }

        });
        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


//        AlertDialog.Builder alter=new AlertDialog.Builder(this);
//        alter.setIcon(R.drawable.logout);
//        alter.setTitle("Log Out");
//        alter.setMessage("Do you want to logout");
//        alter.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(HomeActivity.this, "Not selected", Toast.LENGTH_SHORT).show();
//            }
//        });
//        alter.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(auth!=null){
//                    auth.signOut();
//                    Toast.makeText(HomeActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
//                    finish();
//                }
//            }
//        });
//        alter.show();
    }
}