package com.euro16.Activity.Groupe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.ChoixMondeActivity;
import com.euro16.Activity.Competition.CompetitionActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Groupe;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class CreationGroupeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT < 21) {
            setTheme(R.style.AppTheme);
            getSupportActionBar().setSubtitle(R.string.title_activity_creer_groupe);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_groupe);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        if(Build.VERSION.SDK_INT >= 21) {
            TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
            title.setText(R.string.title_activity_creer_groupe);
            title.setTypeface(face);
        }

        TextView tvNomGroupe = (TextView) findViewById(R.id.textViewNomCommunaute);
        tvNomGroupe.setTypeface(face);

        final EditText editTextNom = (EditText) findViewById(R.id.nomGroupe);
        editTextNom.setTypeface(face);

        Button btnCreerGroupe = (Button) findViewById(R.id.btnCreerGroupe);
        btnCreerGroupe.setTypeface(face);
        if(FacebookConnexion.isOnline(this)) {
            btnCreerGroupe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextNom.getText().toString().length() < 5) {
                        Toast.makeText(getApplicationContext(), "Le nom du groupe doit contenir au moins 5 caractères", Toast.LENGTH_LONG).show();
                    } else {
                        RestClient.creerGroupe(editTextNom.getText().toString(), CurrentSession.utilisateur.getId(), "Image1", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.i("Euro 16", editTextNom.getText().toString() + " : " + CurrentSession.utilisateur.getId());
                                CurrentSession.communaute = null;
                                CurrentSession.groupe = new Groupe(editTextNom.getText().toString(), CurrentSession.utilisateur.getId(), "Image 1");
                                startActivity(new Intent(CreationGroupeActivity.this, CompetitionActivity.class));
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.i("Euro 16", "Impossible de créer le groupe : " + statusCode);
                            }
                        });
                    }
                }
            });
        } else {
            new AlertMsgBox(this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreationGroupeActivity.this, ChoixGroupeActivity.class));
    }
}
