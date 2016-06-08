package com.euro16.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.Communaute.ChoixCommunauteActivity;
import com.euro16.Activity.Competition.CompetitionActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Activity.Groupe.ChoixGroupeActivity;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

public class ChoixMondeActivity extends AppCompatActivity {

    private LinearLayout btnGlobal;

    private TextView labelGlobal;

    private boolean isInGlobal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_monde);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
        title.setText(R.string.title_choix_monde);
        title.setTypeface(face);

        initListenerGlobal();

        labelGlobal = (TextView) findViewById(R.id.btnGlobal);
        labelGlobal.setTypeface(face);

        TextView labelCommunaute = (TextView) findViewById(R.id.btnChoixCommunaute);
        labelCommunaute.setTypeface(face);
        labelCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixCommunauteActivity.class));
            }
        });

        TextView labelGroupe = (TextView) findViewById(R.id.btnGroupe);
        labelGroupe.setTypeface(face);
        labelGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixGroupeActivity.class));
            }
        });

        btnGlobal = (LinearLayout) findViewById(R.id.layout_choix_global);

        LinearLayout btnCommunaute = (LinearLayout) findViewById(R.id.layout_choix_communaute);
        btnCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixCommunauteActivity.class));
            }
        });

        LinearLayout btnGroupe = (LinearLayout) findViewById(R.id.layout_choix_groupe);
        btnGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixGroupeActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.i("Euro 16", "test finish");
        System.exit(0);
    }

    private void choixGlobal(){
        new AlertMsgBox(ChoixMondeActivity.this, "Confirmation", "En validant, vous intégrerez le monde \"Global\" de l'application, et tous les membres pourront voir votre classement. Êtes-vous sûr de continuer ?", "Oui", "Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (FacebookConnexion.isOnline(ChoixMondeActivity.this)) {
                    RestClient.ajouterUtilisateurGlobal(CurrentSession.utilisateur.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            // Ajouté à Global
                            CurrentSession.communaute = null;
                            CurrentSession.groupe = null;
                            startActivity(new Intent(ChoixMondeActivity.this, CompetitionActivity.class));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.i("Euro 16", "failure : " + statusCode + " : " + error.getStackTrace());
                            // Il y a eu un problème lors de l'ajout au global
                        }
                    });
                } else {
                    new AlertMsgBox(ChoixMondeActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                }
            }
        },
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            }
        );
    }

    private void initListenerGlobal(){
        if(FacebookConnexion.isOnline(ChoixMondeActivity.this)) {
            RestClient.getClassementGlobal(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    try{
                        for(int i = 0; i < arrayResponse.length(); i++) {
                            if (arrayResponse.getJSONObject(i).getString("ID_Facebook").equalsIgnoreCase(CurrentSession.utilisateur.getId())) {
                                isInGlobal = true;
                            }
                        }
                        if(isInGlobal){
                            labelGlobal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CurrentSession.communaute = null;
                                    CurrentSession.groupe = null;
                                    startActivity(new Intent(ChoixMondeActivity.this, CompetitionActivity.class));
                                }
                            });
                            btnGlobal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CurrentSession.communaute = null;
                                    CurrentSession.groupe = null;
                                    startActivity(new Intent(ChoixMondeActivity.this, CompetitionActivity.class));
                                }
                            });
                        } else {
                            labelGlobal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    choixGlobal();
                                }
                            });
                            btnGlobal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    choixGlobal();
                                }
                            });
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), "Impossible de récupérer le classement global", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            new AlertMsgBox(ChoixMondeActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }
}
