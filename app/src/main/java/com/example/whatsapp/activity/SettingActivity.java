package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private ImageView savebtn;
    private CircleImageView circleImageView;
    String email;
    private EditText save_name, save_status;

    private Uri selectedimageuri;


    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();


        circleImageView = findViewById(R.id.profile_image_saver);
        save_name = findViewById(R.id.save_name);
        save_status = findViewById(R.id.save_status);


        DatabaseReference reference = database.getReference().child("users").child(auth.getUid());
        StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 email = snapshot.child("email").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String image = snapshot.child("imageURI").getValue().toString();
                save_name.setText(name);
                save_status.setText(status);

                Picasso.get().load(image).into(circleImageView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 11);
            }
        });

        savebtn = findViewById(R.id.save_btn);


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

               String name= save_name.getText().toString();
              String status=  save_status.getText().toString();
                if(selectedimageuri!=null){
                    storageReference.putFile(selectedimageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalimageURI=uri.toString();
                                    User user=new User(auth.getUid(),name,email,finalimageURI,status);
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Data successfully updated", Toast.LENGTH_SHORT).show();
startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                                            }
                                            else{
                                                Toast.makeText(SettingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                }else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String finalimageURI=uri.toString();
                            User user=new User(auth.getUid(),name,email,finalimageURI,status);
                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Data successfully updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                                    }
                                    else{

                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (data != null && resultCode == RESULT_OK) {
                selectedimageuri = data.getData();
                circleImageView.setImageURI(selectedimageuri);
            }
        }
    }
}