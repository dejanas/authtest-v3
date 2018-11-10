package com.etf.ac.bg.rs.sd120456.v3.authtestv3;


import android.app.Fragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.KEYGUARD_SERVICE;

public class VerificationFragment extends Fragment {

    private Context mContext;

    private static final String KEY_NAME = "myKey";
    private Cipher cipher;
    private KeyStore keyStore;
    public boolean proceedRegistration;

    public VerificationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification, container, false);

        AuthTestApplicationV3 mApplication = (AuthTestApplicationV3) getActivity().getApplicationContext();
        FirebaseAuth mAuth = mApplication.getFirebaseAuth();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(mContext.FINGERPRINT_SERVICE);

            TextView textView = (TextView) view.findViewById(R.id.textview);
            TextView textView1 = (TextView) view.findViewById(R.id.textview1);
            TextView textView2 = (TextView) view.findViewById(R.id.textview2);
            TextView putFingerTV = (TextView) view.findViewById(R.id.putFingerTV);

            proceedRegistration = true;

            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                textView.setText("Vaš uređaj ne podržava autentikaciju otiskom prsta.");
                Drawable img1 = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_alert, null );
                img1.setBounds( 0, 0, 60, 60 );
                textView.setCompoundDrawables( img1 , null, null, null );
                textView.setVisibility(View.VISIBLE);
                putFingerTV.setVisibility(View.INVISIBLE);
                proceedRegistration = false;
            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                textView1.setText("Potrebno je dozvoliti autentikaciju otiskom prsta.");
                Drawable img1 = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_alert, null );
                img1.setBounds( 0, 0, 60, 60 );
                textView1.setCompoundDrawables( img1 , null, null, null );
                textView1.setVisibility(View.VISIBLE);
                putFingerTV.setVisibility(View.INVISIBLE);
                proceedRegistration = false;
            }
            int rez = -1;
            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure()) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                textView2.setText("Potrebno je podesiti zaključavanje ekrana u podešavanjima.");
                Drawable img1 = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_alert, null );
                img1.setBounds( 0, 0, 60, 60 );
                textView2.setCompoundDrawables( img1 , null, null, null );
                textView2.setVisibility(View.VISIBLE);
                putFingerTV.setVisibility(View.INVISIBLE);
                proceedRegistration = false;
            } else {
                try {
                    rez = generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
            }
            if (rez == 1 && proceedRegistration){
                if (initCipher()) {
                    //If the cipher is initialized successfully, then create a CryptoObject instance//
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    //Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                    //for starting the authentication process (via the startAuth method) and processing the authentication process events//
                    FingerprintHandler helper = new FingerprintHandler(mContext);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }else{
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null)
                    user.delete();
            }

        }
        return view;
    }

    private int generateKey() throws FingerprintException {
        try {

            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            //keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            if(keyStore != null) {
                //Generate the key//
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                //Initialize an empty KeyStore//
                keyStore.load(null);

                //Initialize the KeyGenerator//
                keyGenerator.init(new

                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());

                //Generate the key//
                keyGenerator.generateKey();

                return 1;
            }
            else{
                return -1;
            }

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            return -1;
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            Log.d("AlgorithmException", e.getMessage());
            return false;
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            Log.d("KEYSTORE", e.getMessage());
            return false;
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
