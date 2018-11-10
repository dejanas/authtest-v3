package com.etf.ac.bg.rs.sd120456.v3.authtestv3;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {

    public boolean mRegistered;
    public boolean mVerified;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        AuthTestApplicationV3 mApplication = (AuthTestApplicationV3)getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();

        mRegistered = false;
        mVerified = false;

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

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        final RegisterFragment regFragment = RegisterFragment.newInstance(tabLayout);

        getFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, regFragment).commit();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment selected;
                int position = tab.getPosition();
                TabLayout.Tab regTab = tabLayout.getTabAt(0);

                if(position == 0) {
                    selected = regFragment;
                    getFragmentManager()
                            .beginTransaction().replace(R.id.fragmentContainer, selected).commit();
                }

                if(position == 1){
                    if(mRegistered) //moze da predje na verifikaciju samo ako se registrovao
                    {
                        selected = new VerificationFragment();
                        getFragmentManager()
                                .beginTransaction().replace(R.id.fragmentContainer, selected).commit();
                    }
                    else
                        if(regTab!=null)
                            regTab.select();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void setVerified(boolean verified){
        mVerified = verified;
    }

    public void setRegistered(boolean registered){
        mRegistered = registered;
    }

    @Override
    public void onBackPressed() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(!mVerified){
            if(user!=null){
                user.delete();
            }
        }
        Intent testIntent = new Intent(this, TestActivity.class);
        startActivity(testIntent);
    }
}


