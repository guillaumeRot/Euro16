package com.euro16.Activity.Communaute;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.Competition.CompetitionActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.Communaute;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.EUtilisateurStatut;
import com.euro16.Utils.ListsView.ListViewAdapterCommunaute;
import com.euro16.Utils.RowsChoix.RowChoixCommunaute;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ChoixCommunauteActivity extends AppCompatActivity {

    private ListViewAdapterCommunaute adapter;

    private HashMap<String, Communaute> hmComUtil;
    private HashMap<String, Integer> hmCommunauteStatut;

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
                    textView.setText("Vous n'êtes inscrit(e) dans aucune communauté");
                    textView.setTextColor(getResources().getColor(R.color.bleu));
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    relLayout.addView(textView);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    // Initialisation de la liste
                    ListView listCommunautesUtil = new ListView(getApplicationContext());
                    listCommunautesUtil.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    TextView textHeader = new TextView(getApplicationContext());
                    textHeader.setText("Vos communautés :");
                    listCommunautesUtil.addHeaderView(textHeader);
                    relLayout.addView(listCommunautesUtil);

                    adapter = new ListViewAdapterCommunaute(ChoixCommunauteActivity.this, R.layout.list_item_communaute);
                    listCommunautesUtil.setAdapter(adapter);

                    // Remplissage de la liste
                    hmComUtil = new HashMap<String, Communaute>();
                    hmCommunauteStatut = new HashMap<String, Integer>();
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            String nomCommunaute = arrayResponse.getJSONObject(i).getString("NomCom");
                            int statutCommUtil = Integer.parseInt(arrayResponse.getJSONObject(i).getString("Statut"));

                            int imageStatutUtil = 0;
                            if(statutCommUtil == EUtilisateurStatut.PARTICIPE.getStatut()) {
                                imageStatutUtil = R.drawable.communaute_participe;
                            } else if(statutCommUtil == EUtilisateurStatut.DEMANDE_PARTICIPE.getStatut()) {
                                imageStatutUtil = R.drawable.communaute_attente;
                            } else if (statutCommUtil == EUtilisateurStatut.EST_INVITE.getStatut()) {
                                lancerDemande(arrayResponse.getJSONObject(i));
                            }

                            if(statutCommUtil == EUtilisateurStatut.PARTICIPE.getStatut() || statutCommUtil == EUtilisateurStatut.DEMANDE_PARTICIPE.getStatut()) {
                                RowChoixCommunaute row = new RowChoixCommunaute(nomCommunaute, imageStatutUtil);
                                adapter.add(row);

                                hmCommunauteStatut.put(nomCommunaute, statutCommUtil);
                                hmComUtil.put(nomCommunaute, new Communaute(nomCommunaute, CurrentSession.utilisateur.getId(), arrayResponse.getJSONObject(i).getString("PhotoCom"), arrayResponse.getJSONObject(i).getString("TypeCom")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listCommunautesUtil.setItemsCanFocus(false);
                    listCommunautesUtil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(position != 0) {
                                String nomCommunaute = adapter.getItem(position - 1).getNom();
                                if (hmCommunauteStatut.get(nomCommunaute) == EUtilisateurStatut.PARTICIPE.getStatut()) {
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

    private void lancerDemande(JSONObject object) {
        try {
            final JSONObject jsonObject = object;
            final String nomCommunaute = jsonObject.getString("NomCom");

            new AlertMsgBox(ChoixCommunauteActivity.this, "Invitation", "Vous avez été invité(e) dans la communauté \"" + nomCommunaute + "\", souhaitez-vous accepter ?", "Oui", "Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (FacebookConnexion.isOnline(ChoixCommunauteActivity.this)) {
                        RestClient.updateStatutUtilisateurCommunaute(nomCommunaute, CurrentSession.utilisateur.getId(), EUtilisateurStatut.PARTICIPE.getStatut(), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    int statutCommUtil = Integer.parseInt(jsonObject.getString("Statut"));
                                    int imageStatutUtil = R.drawable.communaute_participe;

                                    RowChoixCommunaute row = new RowChoixCommunaute(nomCommunaute, imageStatutUtil);
                                    adapter.add(row);

                                    hmCommunauteStatut.put(nomCommunaute, statutCommUtil);
                                    hmComUtil.put(nomCommunaute, new Communaute(nomCommunaute, CurrentSession.utilisateur.getId(), jsonObject.getString("PhotoCom"), jsonObject.getString("TypeCom")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.i("Euro 16", "oui : failure : " + statusCode + " : " + error.getStackTrace());
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
                            if (FacebookConnexion.isOnline(ChoixCommunauteActivity.this)) {
                                RestClient.deleteUtilisateurCommunaute(nomCommunaute, CurrentSession.utilisateur.getId(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        // L'utilisateur a bien été supprimé de la communauté
                                        Toast.makeText(getApplicationContext(), "Invitation rejetée", Toast.LENGTH_SHORT).show();
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
