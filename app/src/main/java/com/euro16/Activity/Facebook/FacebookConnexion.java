package com.euro16.Activity.Facebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.ChoixMondeActivity;
import com.euro16.Activity.Tutoriel.TutorielActivity;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Utilisateur;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.TopCropImageView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class FacebookConnexion extends FragmentActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isOnline(this)) {
            new AlertMsgBox(this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
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
        loginButton = (LoginButton) findViewById(R.id.login_button);
        final RelativeLayout layoutFacebookButton = (RelativeLayout) findViewById(R.id.layoutFacebookButton);

        ImageView superVictor = (ImageView) findViewById(R.id.super_victor);

        if(AccessToken.getCurrentAccessToken() != null){
            layoutFacebookButton.removeView(loginButton);
            superVictor.setImageResource(R.drawable.super_victor_transparent2);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    callMeGraphRequest(AccessToken.getCurrentAccessToken());
                }
            }, 3000);
        } else {
            superVictor.setImageResource(R.drawable.super_victor_transparent1);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutFacebookButton.removeView(loginButton);
            }
        });
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                callMeGraphRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // La connexion a été annulée
            }

            @Override
            public void onError(FacebookException e) {
                // La connexion a échoué
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
        RestClient.creerUtilisateur(CurrentSession.utilisateur.getNom(), CurrentSession.utilisateur.getPrenom(), CurrentSession.utilisateur.getPhoto(), CurrentSession.utilisateur.getId(), new AsyncHttpResponseHandler() {
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
        RestClient.getUtilisateur(CurrentSession.utilisateur.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 204) {
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

    private void callMeGraphRequest(AccessToken token){
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (object.getString("id") != null && object.getString("first_name") != null && object.getString("last_name") != null) {
                                String email = "no email";
                                if (object.has("email")) {
                                    email = object.getString("email");
                                }
                                CurrentSession.utilisateur = new Utilisateur(object.getString("id"), object.getString("last_name"), object.getString("first_name"), email, "http://graph.facebook.com/" + object.getString("id") + "/picture?type=large");
                                getUtilisateur();
                            } else {
                                Toast.makeText(getApplicationContext(), "Impossible de récupérer les données", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
