package com.euro16.Activity.Communaute;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.Competition.CompetitionActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.Communaute;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.ETypeCommunaute;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class CreationCommunauteActivity extends AppCompatActivity {

    private String selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_communaute);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
        title.setText(R.string.title_activity_creer_communaute);
        title.setTypeface(face);

        TextView tvNomCommunaute = (TextView) findViewById(R.id.textViewNomCommunaute);
        tvNomCommunaute.setTypeface(face);

        TextView tvTypeCommunaute = (TextView) findViewById(R.id.textViewTypeCommunaute);
        tvTypeCommunaute.setTypeface(face);

        final EditText editTextNom = (EditText) findViewById(R.id.nomCommunaute);
        editTextNom.setTypeface(face);

        Button btnCreerCommunaute = (Button) findViewById(R.id.btnCreerCommunaute);
        btnCreerCommunaute.setTypeface(face);

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
                    Pattern p = Pattern.compile("[^a-zA-Z0-9\\s]");
                    if (editTextNom.getText().toString().length() < 5) {
                        Toast toast = Toast.makeText(CreationCommunauteActivity.this, getResources().getString(R.string.error_creation_communaute_message), Toast.LENGTH_LONG);
                        TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
                        if( view != null) view.setGravity(Gravity.CENTER);
                        toast.show();
                    } else if(p.matcher(editTextNom.getText()).find() && !editTextNom.getText().toString().contains("é") && !editTextNom.getText().toString().contains("è")) {
                        Toast toast = Toast.makeText(CreationCommunauteActivity.this, getResources().getString(R.string.error_creation_communaute_message), Toast.LENGTH_LONG);
                        TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
                        if( view != null) view.setGravity(Gravity.CENTER);
                        toast.show();
                    } else {
                        RestClient.creerCommunaute(editTextNom.getText().toString(), CurrentSession.utilisateur.getId(), "", selectedType, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
