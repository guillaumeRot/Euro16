package com.euro16.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Utilisateur;
import com.euro16.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {

    private static boolean isConnected = false;
    public static AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        new FetchData().execute();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(isConnected) {
                    startActivity(new Intent(SplashScreenActivity.this, ChoixMondeActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, FacebookConnexion.class));
                }
                finish();
            }
        }, 3000);


    }

    public class FetchData extends AsyncTask<Void, Void, Void> {

        private AccessTokenTracker accessTokenTracker;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    accessToken = AccessToken.getCurrentAccessToken();
                    loadPage();
                }
            };
            accessToken = AccessToken.getCurrentAccessToken();
            loadPage();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public boolean isLoggedIn(){
            return accessToken != null;
        }

        private void loadPage() {
            if(isLoggedIn()) {
                GraphRequest request = GraphRequest.newMeRequest(accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    if(object.getString("id") != null && object.getString("first_name") != null && object.getString("last_name") != null) {
                                        String email = "no email";
                                        if(object.has("email")) {
                                            email = object.getString("email");
                                        }
                                        CurrentSession.utilisateur = new Utilisateur(object.getString("id"), object.getString("last_name"), object.getString("first_name"), email, "http://graph.facebook.com/" + object.getString("id") + "/picture?type=large");
                                        SplashScreenActivity.isConnected = true;
                                        accessTokenTracker.stopTracking();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Impossible de récupérer les données", Toast.LENGTH_SHORT).show();
                                    }

                                } catch(Exception e) {
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

    }
}
