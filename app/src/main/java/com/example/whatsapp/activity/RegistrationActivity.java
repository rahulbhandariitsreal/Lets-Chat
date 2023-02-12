package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {


    private TextView btnsignin,signup;
    private CircleImageView profile_image_register;

    private ProgressDialog progressDialog;

    private Uri imageUri;
    private EditText  ETemail_register,ETpass_register,ET_username_register;

    private FirebaseAuth auth;
    private  final static String EMAIL_PATTERN_Re = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private String imageURI_upload;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        profile_image_register=findViewById(R.id.profile_image_register);
        ET_username_register=findViewById(R.id.ET_username_register);
        ETemail_register=findViewById(R.id.ETemail_register);
        ETpass_register=findViewById(R.id.ETpass_register);
        signup=findViewById(R.id.signup);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);

        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();

        auth=FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name=ET_username_register.getText().toString().trim();
                String pass=ETpass_register.getText().toString().trim();
                String email=ETemail_register.getText().toString().trim();
                String status="Hey There I am using this Application";
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(email)){
                    Toast.makeText(RegistrationActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }else if(pass.length()<6){
                    ETpass_register.setError("Invalid password");
                    Toast.makeText(RegistrationActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else if(!email.matches(EMAIL_PATTERN_Re)){
                    ETemail_register.setError("Invalid email");
                    Toast.makeText(RegistrationActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else{
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                DatabaseReference reference=database.getReference().child("users").child(auth.getUid());
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

                                if(imageUri!=null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    progressDialog.dismiss();
                                                imageURI_upload=uri.toString();
                                                    User user=new User(auth.getUid(),name,email,imageURI_upload,status);
                                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            
                                                            if(task.isSuccessful()){
                                                                startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));

                                                            }else {
                                                                Toast.makeText(RegistrationActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    

                                                }
                                            });
                                        }
                                    });
                                }
                                else{
                                    progressDialog.dismiss();
                                    imageURI_upload="https://firebasestorage.googleapis.co" +
                                            "m/v0/b/whatsapp-230d7.appspot.com/o/defaultprofile.png?alt=media&token=3cd67aee-d6b9-461b-bd3e-bba0f59befeb";
                                    User user=new User(auth.getUid(),name,email,imageURI_upload,status);
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));

                                            }else {
                                                Toast.makeText(RegistrationActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            else{ progressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        profile_image_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Select Picture"),10);

            }
        });

btnsignin=findViewById(R.id.btnSignin);
btnsignin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

    }
});

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data !=null && resultCode==RESULT_OK){
                imageUri=data.getData();
                profile_image_register.setImageURI(imageUri);
            }
        }
    }
}