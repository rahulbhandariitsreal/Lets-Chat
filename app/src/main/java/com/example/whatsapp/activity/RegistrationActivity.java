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

    private  final static String EMAIL_PATTERN_Re = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    //storing profile images
    private FirebaseStorage storage;


    //users for authentication
    private FirebaseAuth auth;

    //main database to store chats and user information
    private FirebaseDatabase database;
    private String imageURI_upload;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        profile_image_register=findViewById(R.id.profile_image_reciever);
        ET_username_register=findViewById(R.id.ET_username_register);
        ETemail_register=findViewById(R.id.ETemail_register);
        ETpass_register=findViewById(R.id.ETpass_register);
        signup=findViewById(R.id.signup);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);

// getting instance of Firebasestorage FirebaseDatabase and  FirebaseAuth
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
                    //using Firebaseauth for creating user by email and password it is not stored anywherw till now
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        //addonComepletelistener is used for checking if the task is succesfull or not
                        //addonsuccess is used for lgetting the details of the task uploaded content
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();


                                //useing firebasedatas reference to store  the user information in the users secrtion by creating a databasereference
                                //and making a child users and a another child the auth uid of that user
                                DatabaseReference reference=database.getReference().child("users").child(auth.getUid());

                                //using Firebasestorage reference to store the uploaded image but first making a child folder of that auth uid to store the image
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());


                                //we always use the reference to store the information

                                //checking if the image is selected or not by the user and responding according to it
                                if(imageUri!=null){

                                    //if image is selected then using the storage reference and putting the file in that folder that we have made above
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            //when image is uploaded in the firebasestorage it is not conneted in any way with the user so we store the token id of the
                                            //image that is uploaded in the firebase in the user section in the databse sso the user can acces the image

                                            //getDownloadUrl is giving the token id of the image just uploaded in the firebasestorage
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    progressDialog.dismiss();
                                                    //converting the uri to string and the saving it in the fireebasedatabase
                                                imageURI_upload=uri.toString();

                                                //making the user object and uploading it in the object format to the firebasedatabase

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

                                    //if the user did not select any image then we will store this uri in the user image uri section
                                    //this uri or image is uploaded by us in the firbasestorage as we can use this image as a default image
                                    //and below is the token of the default image that we will sotre in the user folder
                                    imageURI_upload="https://firebasestorage.googleapis.com/v0/b/whatsapp-230d7.appspot.com/o/defaultprofile.png?alt=media&token=3cd67aee-d6b9-461b-bd3e-bba0f59befeb";
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