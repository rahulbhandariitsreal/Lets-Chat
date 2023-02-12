package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.MessageAdapter;
import com.example.whatsapp.modal.MessageModal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String Recievername,RecieverImage,RecieverUID;

    private RecyclerView messageadapter;
    private CircleImageView profile_image_chat;

    private ArrayList<MessageModal> messageModalArrayList;
    private TextView reciever_name;

    private CardView cardView;
    private EditText EDTmessage;

    private FirebaseDatabase database;
    private FirebaseAuth auth;

    public static  String senderImageURI;
    public static  String recieverImageURI;

    private MessageAdapter myadapter;

    private String senderUID;

    private String senderROOM,recieverROOM;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        cardView=findViewById(R.id.Cardview);
        EDTmessage=findViewById(R.id.EDTmessage);

        messageadapter=findViewById(R.id.messageadapter);
auth=FirebaseAuth.getInstance();

        Recievername=getIntent().getStringExtra("name");
        RecieverImage=getIntent().getStringExtra("ReciecerImage");
        RecieverUID=getIntent().getStringExtra("uid");

        profile_image_chat=findViewById(R.id.profile_image_chat);
        Picasso.get().load(RecieverImage).into(profile_image_chat);

        reciever_name=findViewById(R.id.reciever_name);
        reciever_name.setText(""+Recievername);
        messageModalArrayList=new ArrayList<>();

        senderUID=auth.getUid();

        senderROOM=senderUID+RecieverUID;

        recieverROOM=RecieverUID+senderUID;

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        DatabaseReference reference=database.getReference().child("users").child(auth.getUid());

        DatabaseReference chatreference=database.getReference().child("chats").child(senderROOM).child("messages");
myadapter=new MessageAdapter(this,messageModalArrayList);
LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
linearLayoutManager.setStackFromEnd(true);
messageadapter.setLayoutManager(linearLayoutManager);
messageadapter.setAdapter(myadapter);

chatreference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        messageModalArrayList.clear();

        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
            MessageModal messageModal=dataSnapshot.getValue(MessageModal.class);
messageModalArrayList.add(messageModal);
        }
        myadapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               senderImageURI= snapshot.child("imageURI").getValue().toString();
recieverImageURI=RecieverImage;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=EDTmessage.getText().toString();
if(message.isEmpty()){
    Toast.makeText(ChatActivity.this, "Please enter valid message", Toast.LENGTH_SHORT).show();
    return;
}
EDTmessage.setText("");
                Date date=new Date();
                MessageModal messageModal=new MessageModal(message,senderUID,date.getTime());

                database.getReference().child("chats").child(senderROOM)
                        .child("messages").push().setValue(messageModal).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        database.getReference().child("chats").child(recieverROOM)
                                .child("messages").push().setValue(messageModal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                });

            }
        });


    }
}