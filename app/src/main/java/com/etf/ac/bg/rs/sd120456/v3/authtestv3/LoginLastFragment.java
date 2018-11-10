package com.etf.ac.bg.rs.sd120456.v3.authtestv3;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;


public class LoginLastFragment extends Fragment {

    private TextView proceedTV;
    private Context mContext;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_last_login, container, false);

        AuthTestApplicationV3 mApplication = (AuthTestApplicationV3)mContext.getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();

        proceedTV = (TextView)view.findViewById(R.id.proceedToAnketaTV);
        TextView successLoginTV = (TextView) view.findViewById(R.id.successLoginTV);

        CountDownTimer myCountDown = new CountDownTimer(31000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int)TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                proceedTV.setText("Pokušajte ponovo za: " + seconds);
            }

            public void onFinish() {
                mAuth.signOut();
                proceedTV.setText("Nazad");
                proceedTV.setPaintFlags(proceedTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                proceedTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        proceedTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,null));
                        Intent testIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(testIntent);
                    }
                });
            }
        };

        if(checkIfUserVerified()){
            successLoginTV.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.custom_anim));
            proceedTV.setPaintFlags(proceedTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            proceedTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    proceedTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,null));
                    Intent anketaIntent = new Intent(getActivity(), AnketaActivity.class);
                    startActivity(anketaIntent);
                }
            });
        }else{
            successLoginTV.setText("Neuspešno logovanje.");
            myCountDown.start();
        }
        return view;
    }

    private boolean checkIfUserVerified(){

        Bundle bundle = getArguments();
        String fingerprint = "";
        if(bundle != null)
            fingerprint = bundle.getString("fingerprint");

        if(fingerprint!=null) {
            if (!fingerprint.equals(""))
                if (fingerprint.equals("yes"))
                    return true;
        }

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }
}




