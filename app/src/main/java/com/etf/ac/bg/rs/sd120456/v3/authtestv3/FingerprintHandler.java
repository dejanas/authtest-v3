package com.etf.ac.bg.rs.sd120456.v3.authtestv3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import static android.hardware.fingerprint.FingerprintManager.FINGERPRINT_ERROR_CANCELED;

@TargetApi(Build.VERSION_CODES.M)
class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
    // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

    private CancellationSignal cancellationSignal;
    private Context context;

    FingerprintHandler(Context mContext) {
        context = mContext;
    }

    //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

    void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//

    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        //I’m going to display the results of fingerprint authentication as a series of toasts.
        //Here, I’m creating the message that’ll be displayed if an error occurs//
        if (errMsgId != FINGERPRINT_ERROR_CANCELED) {
            tryAgain(errMsgId);
        }

    }

    @Override

    //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//

    public void onAuthenticationFailed() {
        tryAgain(101);
    }


    @Override

    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        tryAgain(helpMsgId);

    }

    @Override
    //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        Toast.makeText(context, "Uspeh!", Toast.LENGTH_SHORT).show();
        FragmentTransaction tr = ((Activity) context).getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("fingerprint", "yes");

        if(context instanceof  RegisterActivity){
            RegisterLastFragment lastFragment = new RegisterLastFragment();
            lastFragment.setArguments(bundle);
            tr.replace(R.id.fragmentContainer, lastFragment);
        }
        if(context instanceof  LoginActivity){
            LoginLastFragment lastFragment = new LoginLastFragment();
            lastFragment.setArguments(bundle);
            tr.replace(R.id.fragmentContainerLogin, lastFragment);
        }

        tr.commit();
    }

    private void tryAgain(int errorMsgId){

        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();

        String errorMsg="";
        switch (errorMsgId){

            case 1:
                errorMsg = "Pokušajte ponovo: prislonite prst celom površinom.";
                startVerificationAgain(fragmentManager);
                break;
            case 2: case 3:
                errorMsg = "Pokušajte ponovo: obrišite senzor.";
                startVerificationAgain(fragmentManager);
                break;
            case 5:
                errorMsg = "Pokušajte ponovo: zadržite prst na senzoru.";
                startVerificationAgain(fragmentManager);
                break;
            case 101:
                errorMsg = "Bez poklapanja. Pokušajte ponovo.";
                startVerificationAgain(fragmentManager);
                break;
            case 7:
                errorMsg = "Previše pokušaja. Probajte kasnije.";
                cancellationSignal.cancel();
                startLastFragment(fragmentManager);
        }

        Toast.makeText(context, errorMsg , Toast.LENGTH_SHORT).show();


    }

    private void startVerificationAgain(FragmentManager fragmentManager){
        if(context instanceof  RegisterActivity){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, new VerificationFragment()).commit();
        }

        if(context instanceof  LoginActivity){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerLogin, new VerificationFragment()).commit();
        }
    }

    private void startLastFragment(FragmentManager fragmentManager){

        if(context instanceof  RegisterActivity){
            RegisterLastFragment lastFragment = new RegisterLastFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, lastFragment).commit();

        }
        if(context instanceof  LoginActivity){
            LoginLastFragment lastFragment = new LoginLastFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerLogin, lastFragment).commit();
        }


    }
}
