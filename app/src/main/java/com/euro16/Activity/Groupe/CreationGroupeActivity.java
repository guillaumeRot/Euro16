package com.euro16.Activity.Groupe;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.CompetitionActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Groupe;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.EMonde;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class CreationGroupeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_groupe);

        getSupportActionBar().setSubtitle(R.string.title_activity_creer_groupe);

        final EditText editTextNom = (EditText) findViewById(R.id.nomGroupe);

        Button btnCreerGroupe = (Button) findViewById(R.id.btnCreerGroupe);
        if(FacebookConnexion.isOnline(this)) {
            btnCreerGroupe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextNom.getText().toString().length() < 5) {
                        Toast.makeText(getApplicationContext(), "Le nom de groupe est incorrect", Toast.LENGTH_SHORT).show();
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
}
