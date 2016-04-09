package com.euro16.Activity;

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

        final EditText editTextNom = (EditText) findViewById(R.id.nomGroupe);

        Button btnCreerGroupe = (Button) findViewById(R.id.btnCreerGroupe);
        if(FacebookConnexion.isOnline(this)) {
            btnCreerGroupe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextNom.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Veuillez mettre un nom de groupe", Toast.LENGTH_SHORT).show();
                    } else {
                        RestClient.creerGroupe(editTextNom.getText().toString(), FacebookConnexion.profil.getId(), "Image1", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.i("Euro 16", editTextNom.getText().toString() + " : " + FacebookConnexion.profil.getId());
                                CompetitionActivity.nomCurrentMonde = editTextNom.getText().toString();
                                CompetitionActivity.typeCurrentMonde = EMonde.GROUPE.getTypeMonde();
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
