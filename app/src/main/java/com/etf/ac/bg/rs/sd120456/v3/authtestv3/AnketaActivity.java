package com.etf.ac.bg.rs.sd120456.v3.authtestv3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AnketaActivity extends AppCompatActivity implements AnketaInputFragment.OnDataPass{

    public Context mContext;
    private boolean mHasAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anketa);

        mContext =  AnketaActivity.this;
        mHasAnswer = false;

        AuthTestApplicationV3 mApplication = (AuthTestApplicationV3)mContext.getApplicationContext();
        FirebaseAuth mAuth = mApplication.getFirebaseAuth();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String username;

        if(mUser!=null){
            username = mUser.getDisplayName();
        }else{
            username = "anonymous";
        }

        PreferenceManager.getDefaultSharedPreferences(mContext).edit().clear().apply();
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("username", username).apply();

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
        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerAnketa, new AnketaInputFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(mHasAnswer)
            showDialogBox();
        else{
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }


    }

    public void showDialogBox(){
        new AlertDialog.Builder(this)
                .setTitle("Napomena")
                .setMessage("Uneti odgovori će biti izbrisani.")
                .setPositiveButton("Ostani", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("U redu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent backIntent = new Intent(mContext, MainActivity.class);
                        startActivity(backIntent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onDataPass(boolean hasAnswer) {
        mHasAnswer = hasAnswer;
    }
}
