package com.gmailcymbidiumm.stayindoorsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private TextView mStayIndoorsText,mLoginText;
    private EditText mNameEditText,mEmailEdittext,mPasswordEditText;
    private Button mRegisterBtn;
    private ProgressBar progressBar;
    FirebaseAuth fAuth;
    private static final String TAG = Register.class.getSimpleName();

    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mStayIndoorsText = findViewById(R.id.stayIndoorsText);
        mLoginText = findViewById(R.id.loginText);
        mNameEditText = findViewById(R.id.nameEditText);
        mEmailEdittext = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mRegisterBtn = findViewById(R.id.registerBtn);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

//        for the user who is already logged in

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mNameEditText.getText().toString().trim();
                final String email = mEmailEdittext.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    mNameEditText.setError("Hei! you have not entered your name. Go do that, you mystical being.");
                }

                if(TextUtils.isEmpty(email)){
                    mEmailEdittext.setError("Pretty please enter your email.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPasswordEditText.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6){
                    mPasswordEditText.setError("Password must be >= 6 characters. shocking, huh?");
                    return;
                }

                register(email, password, name);
            }
        });

        mLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));

            }
        });
    }

    private void register(final String email, final String password, final String name) {

//        register the user in Firebase


        progressBar.setVisibility(View.VISIBLE);

        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {


//              send verification link -email

                    FirebaseUser fuser = fAuth.getCurrentUser();

                    if(fuser != null) {
                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Register.this,"Verification email has been sent.",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"onFailure: Email not sent " + e.getMessage());
                            }
                        });

                        Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                    }


                    storeInDb(email, name);


                }else{
                    Toast.makeText(Register.this,"Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

    private void storeInDb(final String email, final String name) {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if(currentUser != null) {
            userID = currentUser.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users").child(userID);

            Map<String,Object> user = new HashMap<>();
            user.put("fName",name);
            user.put("email",email);
            user.put("imageurl","https://firebasestorage.googleapis.com/v0/b/stay-indoors-project.appspot.com/o/brain_PNG71.png?alt=media&token=dd0587d1-5835-4317-9332-a60f314211d5");
            reference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG,"user Profile is created for " + userID );

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
        }


    }
}
