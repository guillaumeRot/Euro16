package com.euro16.Activity.Communaute;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.CompetitionActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.Communaute;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.EMonde;
import com.euro16.Utils.ETypeCommunaute;
import com.euro16.Utils.EUtilisateurStatut;
import com.euro16.Utils.ListViewAdapterCommunaute;
import com.euro16.Utils.RowChoixCommunaute;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ChoixCommunauteActivity extends AppCompatActivity {

    private boolean noItemSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_communaute);

        getSupportActionBar().setSubtitle(R.string.title_activity_choix_communaute);

        final RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.relLayout);

        if(FacebookConnexion.isOnline(this)) {
            RestClient.getCommunautesUtilisateur(CurrentSession.utilisateur.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText("Vous n'êtes inscrit dans aucune communauté");
                    textView.setTextColor(getResources().getColor(R.color.bleu));
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    relLayout.addView(textView);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    for(int i = 0; i< arrayResponse.length(); i++) {
                        try {
                            final String nomCommunaute = arrayResponse.getJSONObject(i).getString("NomCom");
                            int statutCommUtil = Integer.parseInt(arrayResponse.getJSONObject(i).getString("Statut"));
                            if (statutCommUtil == EUtilisateurStatut.EST_INVITE.getStatut()) {
                                new AlertMsgBox(ChoixCommunauteActivity.this, "Invitation", "Vous avez été invité dans la communauté \"" + nomCommunaute + "\", voulez-vous accepter ?", "Oui", "Non", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(FacebookConnexion.isOnline(ChoixCommunauteActivity.this)) {
                                            RestClient.updateStatutUtilisateurCommunaute(nomCommunaute, CurrentSession.utilisateur.getId(), EUtilisateurStatut.PARTICIPE.getStatut(), new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.i("Euro 16", "oui : on success : " + statusCode);
                                                    // L'utilisateur participe à cette communauté
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.i("Euro 16", "oui : onFailure : " + statusCode);
                                                    // Il y a eu un problème lors de la confirmation d'invitation
                                                }
                                            });
                                        } else {
                                            new AlertMsgBox(ChoixCommunauteActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
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
                                        if(FacebookConnexion.isOnline(ChoixCommunauteActivity.this)) {
                                            RestClient.deleteUtilisateurCommunaute(nomCommunaute, CurrentSession.utilisateur.getId(), new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.i("Euro 16", "non : on success : " + statusCode);
                                                    // L'utilisateur a bien été supprimé de la communauté
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.i("Euro 16", "non : onFailure : " + statusCode);
                                                    // Il y a eu un problème lors de la suppression d'invitation
                                                }
                                            });
                                        } else {
                                            new AlertMsgBox(ChoixCommunauteActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
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
                    ListView listCommunautesUtil = new ListView(getApplicationContext());
                    listCommunautesUtil.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    TextView textHeader = new TextView(getApplicationContext());
                    textHeader.setText("Vos communautés :");
                    listCommunautesUtil.addHeaderView(textHeader);
                    relLayout.addView(listCommunautesUtil);

                    final ListViewAdapterCommunaute adapter = new ListViewAdapterCommunaute(ChoixCommunauteActivity.this, R.layout.list_item_communaute);
                    listCommunautesUtil.setAdapter(adapter);

                    final HashMap<String, Communaute> hmComUtil = new HashMap<String, Communaute>();
                    final HashMap<String, Integer> hmCommunauteStatut = new HashMap<String, Integer>();
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            String nomCommunaute = arrayResponse.getJSONObject(i).getString("NomCom");
                            int statutCommUtil = Integer.parseInt(arrayResponse.getJSONObject(i).getString("Statut"));

                            int imageStatutUtil = 0;
                            if(statutCommUtil == EUtilisateurStatut.PARTICIPE.getStatut()) {
                                imageStatutUtil = R.drawable.communaute_participe;
                            } else if(statutCommUtil == EUtilisateurStatut.DEMANDE_PARTICIPE.getStatut()) {
                                imageStatutUtil = R.drawable.communaute_attente;
                            }

                            RowChoixCommunaute row = new RowChoixCommunaute(nomCommunaute, imageStatutUtil);
                            adapter.add(row);

                            hmCommunauteStatut.put(nomCommunaute, statutCommUtil);
                            hmComUtil.put(nomCommunaute, new Communaute(nomCommunaute, CurrentSession.utilisateur.getId(), arrayResponse.getJSONObject(i).getString("PhotoCom"), arrayResponse.getJSONObject(i).getString("TypeCom")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listCommunautesUtil.setItemsCanFocus(false);
                    listCommunautesUtil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String nomCommunaute = adapter.getItem(position-1).getNom();
                            if(hmCommunauteStatut.get(nomCommunaute) == EUtilisateurStatut.PARTICIPE.getStatut()) {
                                CurrentSession.groupe = null;
                                CurrentSession.communaute = new Communaute(nomCommunaute, CurrentSession.utilisateur.getId(), hmComUtil.get(nomCommunaute).getPhoto(), hmComUtil.get(nomCommunaute).getType());
                                startActivity(new Intent(ChoixCommunauteActivity.this, CompetitionActivity.class));
                            } else if (hmCommunauteStatut.get(nomCommunaute) == EUtilisateurStatut.DEMANDE_PARTICIPE.getStatut()) {
                                new AlertMsgBox(ChoixCommunauteActivity.this, "En attente", "L'administrateur de la communauté \"" + nomCommunaute + "\" n'a pas encore validé votre demande, vous ne pouvez donc pas encore rejoindre ce groupe.", "ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }
                                });
                            }
                        }
                    });
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

        Button btnRejoindreCommunaute = (Button) findViewById(R.id.btnRejoindreCommunaute);
        btnRejoindreCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixCommunauteActivity.this, RejoindreCommunauteActivity.class));
            }
        });

        Button btnCreerCommunaute = (Button) findViewById(R.id.btnCreerCom);
        btnCreerCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixCommunauteActivity.this, CreationCommunauteActivity.class));
            }
        });

    }
}
