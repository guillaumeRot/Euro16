package com.euro16.Activity.Communaute;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.ChoixMondeActivity;
import com.euro16.Activity.Competition.CompetitionActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.Communaute;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.ETypeCommunaute;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CreationCommunauteActivity extends AppCompatActivity {

    private String selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT < 21) {
            setTheme(R.style.AppTheme);
            getSupportActionBar().setSubtitle(R.string.title_activity_creer_communaute);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_communaute);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        if(Build.VERSION.SDK_INT >= 21) {
            TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
            title.setText(R.string.title_activity_creer_communaute);
            title.setTypeface(face);
        }

        final EditText editTextNom = (EditText) findViewById(R.id.nomCommunaute);
        Button btnCreerCommunaute = (Button) findViewById(R.id.btnCreerCommunaute);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<String> listSpinner = new ArrayList<String>();
        listSpinner.add(ETypeCommunaute.PUBLIQUE.getNomType());
        listSpinner.add(ETypeCommunaute.PRIVEE.getNomType());

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (adapter.getItem(position).toString().equalsIgnoreCase("Publique")) {
                    selectedType = ETypeCommunaute.PUBLIQUE.getTypeBase();
                } else if (adapter.getItem(position).toString().equalsIgnoreCase("Privée")) {
                    selectedType = ETypeCommunaute.PRIVEE.getTypeBase();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        if(FacebookConnexion.isOnline(this)) {
            btnCreerCommunaute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextNom.getText().toString().length() < 5) {
                        Toast.makeText(getApplicationContext(), "Le nom de la communauté doit contenir au moins 5 caractères", Toast.LENGTH_LONG).show();
                    } else {
                        RestClient.creerCommunaute(editTextNom.getText().toString(), CurrentSession.utilisateur.getId(), "", selectedType, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.i("Euro 16", editTextNom.getText().toString() + " : " + CurrentSession.utilisateur.getId() + " : " + selectedType);
                                CurrentSession.groupe = null;
                                CurrentSession.communaute = new Communaute(editTextNom.getText().toString(), CurrentSession.utilisateur.getId(), "", selectedType);
                                startActivity(new Intent(CreationCommunauteActivity.this, CompetitionActivity.class));
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.i("Euro 16", "Impossible de créer la communauté : " + statusCode);
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
        startActivity(new Intent(CreationCommunauteActivity.this, ChoixCommunauteActivity.class));
    }
}
