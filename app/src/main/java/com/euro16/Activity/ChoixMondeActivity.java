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
import com.euro16.Model.Communaute;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.EUtilisateurStatut;
import com.euro16.Utils.RowsChoix.RowChoixCommunaute;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class ChoixMondeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_monde);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
        title.setText(R.string.title_choix_monde);
        title.setTypeface(face);

        TextView labelGlobal = (TextView) findViewById(R.id.btnGlobal);
        labelGlobal.setTypeface(face);
        labelGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choixGlobal();
            }
        });

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

        LinearLayout btnGlobal = (LinearLayout) findViewById(R.id.layout_choix_global);
        btnGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choixGlobal();
            }
        });

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
}
