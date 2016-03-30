package com.euro16.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.euro16.API.RestClient;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FacebookConnexion extends FragmentActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView info;

    public static Profile profil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isOnline(this)) {
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
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    final ProfileTracker profilTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile1) {
                            profil = profile1;
                            getUtilisateur();
                            this.stopTracking();
                        }
                    };
                    profilTracker.startTracking();
                } else {
                    profil = Profile.getCurrentProfile();
                    getUtilisateur();
                }
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

    public void creationUtilisateur() {
        RestClient.creerUtilisateur(profil.getLastName(), profil.getFirstName(), profil.getProfilePictureUri(300, 300).toString(), profil.getId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // L'utilisateur a bien été ajouté dans la base
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Euro 16", "Impossible de créer l'utilisateur, statut : " + statusCode);
            }
        });
    }

    public void getUtilisateur() {
        RestClient.getUtilisateur(profil.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(statusCode == 204) {
                    creationUtilisateur();
                    startActivity(new Intent(FacebookConnexion.this, TutorielActivity.class));
                } else {
                    startActivity(new Intent(FacebookConnexion.this, ChoixMondeActivity.class));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("Euro 16", "Impossible de récupérer l'utilisateur, statut : " + statusCode);
            }
        });
    }
}
