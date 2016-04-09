package com.euro16.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.EMonde;
import com.euro16.Utils.EUtilisateurStatut;
import com.facebook.Profile;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class RejoindreCommunauteActivity extends AppCompatActivity {

    private boolean noItemSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_communaute);

        final ListView listCommunautes = (ListView) findViewById(R.id.listCommunautes);
        final ArrayAdapter adapter = new ArrayAdapter(RejoindreCommunauteActivity.this, android.R.layout.simple_list_item_single_choice);
        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override
            public void afterTextChanged(Editable arg0) {}
        });

        if(FacebookConnexion.isOnline(this)) {
            RestClient.getCommunautes(FacebookConnexion.profil.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    if (statusCode == 204) {
                        Log.i("Euro 16", "Vous n'êtes inscrit dans aucune communauté");
                    } else {
                        Log.i("Euro 16", "status : " + statusCode + "\n" + "response : " + arrayResponse.toString());
                        listCommunautes.setAdapter(adapter);
                        for (int i = 0; i < arrayResponse.length(); i++) {
                            try {
                                adapter.add(arrayResponse.getJSONObject(i).getString("NomCom"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listCommunautes.setItemsCanFocus(false);
                        listCommunautes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        listCommunautes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                CompetitionActivity.nomCurrentMonde = adapter.getItem(position).toString();
                                noItemSelected = false;
                            }
                        });
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("Euro 16", "Impossible de récupérer les communautés de l'utilisateur, statut : " + statusCode);
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

        if(FacebookConnexion.isOnline(this)) {
            Button btnRejoindreCommunaute = (Button) findViewById(R.id.btnRejoindreComm);
            btnRejoindreCommunaute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!noItemSelected) {
                        RestClient.ajouterUtilisateurCommunaute(FacebookConnexion.profil.getId(), CompetitionActivity.nomCurrentMonde, EUtilisateurStatut.DEMANDE_PARTICIPE.getStatut(), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Log.i("Euro 16", "statut : " + statusCode);
                                CompetitionActivity.typeCurrentMonde = EMonde.COMMUNAUTE.getTypeMonde();
                                startActivity(new Intent(RejoindreCommunauteActivity.this, CompetitionActivity.class));
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.i("Euro 16", "Impossible de demander l'ajout à la communauté : " + statusCode);
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Aucune communauté n'est séléctionné", Toast.LENGTH_SHORT).show();
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
