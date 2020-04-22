package com.gmailcymbidiumm.stayindoorsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gmailcymbidiumm.stayindoorsapp.Fragment.HomeFragment;
import com.gmailcymbidiumm.stayindoorsapp.Fragment.NotificationFragment;
import com.gmailcymbidiumm.stayindoorsapp.Fragment.ProfileFragment;
import com.gmailcymbidiumm.stayindoorsapp.Fragment.SearchFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.fragment.app.Fragment;


public class MainActivity extends AppCompatActivity {

    TextView resendCodeBtn;
    TextView verifyMsg;
    FirebaseAuth fAuth;

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        resendCodeBtn = findViewById(R.id.resendCodeBtn);
        verifyMsg = findViewById(R.id.verifyMsg);

        fAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = fAuth.getCurrentUser();

        if(!user.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            resendCodeBtn.setVisibility(View.VISIBLE);

            verifyMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            resendCodeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {


                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(view.getContext(),"Verification email has been sent.",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag","onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }


    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;

                        case R.id.nav_add:
                            selectedFragment = null;
                            startActivity(new Intent(MainActivity.this, PostActivity.class));
                            break;

                        case R.id.nav_archive:
                            selectedFragment = new NotificationFragment();
                            break;

                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREPS",MODE_PRIVATE).edit();
                            editor.putString("profileid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            break;

                    }

                    if(selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    }


                    return true;
                }
            };
}

