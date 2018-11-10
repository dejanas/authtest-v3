package com.etf.ac.bg.rs.sd120456.v3.authtestv3;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;


public class RegisterLastFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Context mContext;

    private TextView proceedToLoginTV;
    private TextView goBackTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_last_register, container, false);

        AuthTestApplicationV3 mApplication = (AuthTestApplicationV3)mContext.getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();

        proceedToLoginTV = (TextView)view.findViewById(R.id.proceedToLoginTV);
        TextView successRegisteredTV = (TextView) view.findViewById(R.id.successRegisteredTV);
        proceedToLoginTV.setPaintFlags(proceedToLoginTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        goBackTV = (TextView)view.findViewById(R.id.goBackTV);

        CountDownTimer myCountDown = new CountDownTimer(31000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                goBackTV.setText("Pokušajte ponovo za: " + seconds);
            }

            public void onFinish() {
                goBackTV.setText("Nazad");
                goBackTV.setPaintFlags(goBackTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                goBackTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent testIntent = new Intent(getActivity(), RegisterActivity.class);
                        startActivity(testIntent);
                    }
                });
            }
        };

        if(checkIfUserVerified()){
            ((RegisterActivity)getActivity()).setVerified(true);
            successRegisteredTV.setVisibility(View.VISIBLE);
            successRegisteredTV.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.custom_anim));
            proceedToLoginTV.setVisibility(View.VISIBLE);

        }else{
            deleteCurrentUser();
            successRegisteredTV.setText("Neuspešna registracija.");
            successRegisteredTV.setVisibility(View.VISIBLE);
            goBackTV.setVisibility(View.VISIBLE);
            myCountDown.start();


        }

        proceedToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToLoginTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,null));
                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        goBackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,null));
                Intent registerActivity = new Intent(mContext, RegisterActivity.class);
                startActivity(registerActivity);
            }
        });

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

    public void deleteCurrentUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Ponovo.(obrisan)", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
