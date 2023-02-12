package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextView btnSignup;

    private EditText ETemail_login,ETpass_login;

    private FirebaseAuth auth;

    private  final static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private ProgressDialog progressDialog;

    private TextView signinbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
ETemail_login=findViewById(R.id.ETemail_register);
ETpass_login=findViewById(R.id.ETpass_register);
signinbtn=findViewById(R.id.signinbtn);

progressDialog=new ProgressDialog(this);
progressDialog.setMessage("Please wait...");
progressDialog.setCancelable(false);
auth=FirebaseAuth.getInstance();

signinbtn.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {
        progressDialog.show();
        String email=ETemail_login.getText().toString().trim();
        String passwrod=ETpass_login.getText().toString().trim();

        if(TextUtils.isEmpty(email)  ||  TextUtils.isEmpty(passwrod)){
            Toast.makeText(LoginActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        } else if (!email.matches(EMAIL_PATTERN)) {
            ETemail_login.setError("Invalid email");
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();

        } else if(passwrod.length()<6){
            progressDialog.dismiss();
            ETpass_login.setError("Invalid password");
            Toast.makeText(LoginActivity.this, "Password less than six character", Toast.LENGTH_SHORT).show();
        }
        else{

            auth.signInWithEmailAndPassword(email,passwrod).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error in login", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                    return;
                }
            });

        }
    }
});


        btnSignup=findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }
}