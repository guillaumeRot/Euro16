package com.euro16.Activity.Groupe;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.CompetitionActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Groupe;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.EMonde;
import com.euro16.Utils.EUtilisateurStatut;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ChoixGroupeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_groupe);

        getSupportActionBar().setSubtitle(R.string.title_activity_choix_groupe);

        final RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.relLayout);

        if(FacebookConnexion.isOnline(this)) {
            RestClient.getGroupesUtilisateur(CurrentSession.utilisateur.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText("Vous n'êtes inscrit dans aucun groupe");
                    textView.setTextColor(getResources().getColor(R.color.bleu));
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    relLayout.addView(textView);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    for(int i = 0; i< arrayResponse.length(); i++) {
                        try {
                            final String nomGroupe = arrayResponse.getJSONObject(i).getString("NomGrp");
                            int statutGroupeUtil = Integer.parseInt(arrayResponse.getJSONObject(i).getString("Statut"));
                            if (statutGroupeUtil == EUtilisateurStatut.EST_INVITE.getStatut()) {
                                new AlertMsgBox(ChoixGroupeActivity.this, "Invitation", "Vous avez été invité dans le groupe \"" + nomGroupe + "\", voulez-vous accepter ?", "Oui", "Non", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(FacebookConnexion.isOnline(ChoixGroupeActivity.this)) {
                                            RestClient.updateStatutUtilisateurCommunaute(nomGroupe, CurrentSession.utilisateur.getId(), EUtilisateurStatut.PARTICIPE.getStatut(), new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.i("Euro 16", "oui : on success : " + statusCode);
                                                    // L'utilisateur participe à ce groupe
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.i("Euro 16", "oui : onFailure : " + statusCode);
                                                    // Il y a eu un problème lors de la confirmation d'invitation
                                                }
                                            });
                                        } else {
                                            new AlertMsgBox(ChoixGroupeActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
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
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(FacebookConnexion.isOnline(ChoixGroupeActivity.this)) {
                                                    RestClient.deleteUtilisateurCommunaute(nomGroupe, CurrentSession.utilisateur.getId(), new AsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            Log.i("Euro 16", "non : on success : " + statusCode);
                                                            // L'utilisateur a bien été supprimé du groupe
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                            Log.i("Euro 16", "non : onFailure : " + statusCode);
                                                            // Il y a eu un problème lors de la suppression d'invitation
                                                        }
                                                    });
                                                } else {
                                                    new AlertMsgBox(ChoixGroupeActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                );
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    ListView listGroupes = new ListView(getApplicationContext());
                    listGroupes.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    TextView textHeader = new TextView(getApplicationContext());
                    textHeader.setText("Vos groupes :");
                    listGroupes.addHeaderView(textHeader);
                    relLayout.addView(listGroupes);
                    final ArrayAdapter adapter = new ArrayAdapter(ChoixGroupeActivity.this, android.R.layout.simple_list_item_1);
                    listGroupes.setAdapter(adapter);
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            adapter.add(arrayResponse.getJSONObject(i).getString("NomGrp"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listGroupes.setItemsCanFocus(false);
                    listGroupes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CurrentSession.communaute = null;
                            CurrentSession.groupe = new Groupe(parent.getItemAtPosition(position).toString(), CurrentSession.utilisateur.getId(), "Image 1");
                            startActivity(new Intent(ChoixGroupeActivity.this, CompetitionActivity.class));
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("Euro 16", "Impossible de récupérer les groupes de l'utilisateur, statut : " + statusCode);
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

        Button btnCreerGroupe = (Button) findViewById(R.id.btnCreerGroupe);
        btnCreerGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixGroupeActivity.this, CreationGroupeActivity.class));
            }
        });
    }
}
