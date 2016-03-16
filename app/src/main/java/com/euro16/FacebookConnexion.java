package com.euro16;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class FacebookConnexion extends FragmentActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView info;

    private static final String TAG = "Euro16";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isOnline(this)) {
            new AlertMsgBox(this, "Pas de réseau", "Aucune connexion internet n'a été trouvé, veuillez réessayer lorsque la connexion sera rétablie.", "ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_facebook_connexion);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n"
                        + "Auth Token: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                info.setText("La connexion a été annulée.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Une erreur est survenue lors de la connexion.");
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
