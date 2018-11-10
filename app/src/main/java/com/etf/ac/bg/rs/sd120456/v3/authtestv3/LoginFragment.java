package com.etf.ac.bg.rs.sd120456.v3.authtestv3;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Context mContext;
    CallbackManager callbackManager;
    private FirebaseUser mUser;
    private LoginManager loginManager;

    private EditText emailET;
    private EditText passwordET;
    private TextView loginInfoNameTV;
    private TextView loginInfoEmailTV;
    private Button logoutBtn;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        AuthTestApplicationV3 mApplication = (AuthTestApplicationV3) mContext.getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();
        mUser = mAuth.getCurrentUser();
        loginManager = mApplication.getLoginManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_pass_login, container, false);

        emailET = (EditText) view.findViewById(R.id.log_email_ET);
        passwordET = (EditText) view.findViewById(R.id.log_password_ET);

        loginInfoNameTV = (TextView) view.findViewById(R.id.loginInfoName);
        loginInfoEmailTV = (TextView) view.findViewById(R.id.loginInfoEmail);

        Button loginBtn = (Button) view.findViewById(R.id.login_btn);
        logoutBtn = (Button) view.findViewById(R.id.logoutBtn);

        loginBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        if (mUser != null) {
            String displayName = mUser.getDisplayName();
            if (displayName == null)
                displayName = "";
            String loginInfo = "Ulogovani ste sa nalogom: " + displayName;
            loginInfoNameTV.setText(loginInfo);
            loginInfoEmailTV.setText(" (" + mUser.getEmail() + ")");
            loginInfoNameTV.setVisibility(View.VISIBLE);
            loginInfoNameTV.setVisibility(View.VISIBLE);
        } else {
            loginInfoNameTV.setVisibility(View.GONE);
            loginInfoEmailTV.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login_btn:
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if (validateInputs(email, password))
                    loginUserWithEmailAndPassword(email, password);

                break;

            case R.id.logoutBtn:
                if (mUser != null) {
                    mAuth.signOut();
                    loginManager.logOut();
                    loginInfoNameTV.setText("Izlogovali ste se.");
                    loginInfoEmailTV.setVisibility(View.GONE);
                    logoutBtn.setVisibility(View.GONE);
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void moveToVerification(String email) {
        emailET.setText(email);
        passwordET.setText("");


        ((LoginActivity) mContext).setLogedIn(true);
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.replace(R.id.fragmentContainerLogin, new VerificationFragment()).commit();
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        if (email.equals("")) {
            emailET.setError(getString(R.string.email_insert_warning));
            isValid = false;
        }
        if (password.equals("")) {
            passwordET.setError(getString(R.string.insert_password_warning));
            isValid = false;
        }
        return isValid;
    }

    private void loginUserWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null) {
                                Toast.makeText(mContext, "Login successful.", Toast.LENGTH_SHORT).show();
                                moveToVerification(user.getEmail());
                            }else{
                                Toast.makeText(mContext, "Login not successful.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            String errorMsg = "";
                            if (task.getException() != null)
                                errorMsg += task.getException().getMessage();
                            else
                                errorMsg = "unknown error.";
                            Toast.makeText(mContext, "Login failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                });

    }

}