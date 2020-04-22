package com.gmailcymbidiumm.stayindoorsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    TextView mStayIndoorsText, mCreateAccText, forgotTextLink;
    EditText mEmailEditText,mPasswordEditText;
    Button mLogInBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mStayIndoorsText = findViewById(R.id.stayIndoorsText) ;
        mCreateAccText = findViewById(R.id.createAccText);
        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mLogInBtn = findViewById(R.id.logInBtn);
        progressBar = findViewById(R.id.progressBar);
        forgotTextLink = findViewById(R.id.forgotPassword);

        fAuth = FirebaseAuth.getInstance();

        mLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmailEditText.setError("Pretty please enter your email.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPasswordEditText.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6){
                    mPasswordEditText.setError("Password must be >= 6 characters");
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);

//                authenticate the user

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Logged in Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));


                        }else{
                            Toast.makeText(Login.this,"Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });


        mCreateAccText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));

            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter your email to receive reset link.");
                passwordResetDialog.setView(resetMail);



                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        extract the email and send the reset link

                        String mail = resetMail.getText().toString();

                        if(!mail.equals("")) {

//

                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Login.this, "Reset Link sent to your email. ", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "Error ! Reset Link is not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }else{

                            Toast.makeText(Login.this, "Oops, you have not entered the email !" , Toast.LENGTH_SHORT).show();

                        }
                    }

                });


                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        close the dialog
                    }
                });

                passwordResetDialog.create().show();


            }
        });

    }
}
