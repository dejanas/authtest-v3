package com.etf.ac.bg.rs.sd120456.v3.authtestv3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private boolean logedIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logedIn = false;
        AuthTestApplicationV3 mApplication = (AuthTestApplicationV3)getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();

        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerLogin, new LoginFragment()).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setLogedIn(boolean logedIn){
        this.logedIn = logedIn;
    }

    @Override
    public void onBackPressed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            mAuth.signOut();
        }
        Intent testIntent = new Intent(this, TestActivity.class);
        startActivity(testIntent);
    }
}
