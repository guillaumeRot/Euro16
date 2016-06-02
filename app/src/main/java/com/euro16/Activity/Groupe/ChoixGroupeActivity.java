package com.euro16.Activity.Groupe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.euro16.Utils.Enums.EUtilisateurStatut;
import com.euro16.Utils.ListsView.ArrayAdapterString;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ChoixGroupeActivity extends AppCompatActivity {

    private ArrayAdapterString adapter;

    private HashMap<String, Groupe> hmGrpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT < 21) {
            setTheme(R.style.AppTheme);
            getSupportActionBar().setSubtitle(R.string.title_activity_choix_groupe);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_groupe);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        TextView tvDejaInscrit = (TextView) findViewById(R.id.tvDejaInscrit);
        tvDejaInscrit.setTypeface(face);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        if(Build.VERSION.SDK_INT >= 21) {
            TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
            title.setText(R.string.title_activity_choix_groupe);
            title.setTypeface(face);
        }

        final RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.relLayout);


        if(FacebookConnexion.isOnline(this)) {

            RestClient.getGroupesUtilisateur(CurrentSession.utilisateur.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {

                    Toast toast = Toast.makeText(ChoixGroupeActivity.this, "Vous n'êtes inscrit(e) dans aucun groupe", Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

//                    TextView textView = new TextView(getApplicationContext());
//                    textView.setText("Vous n'êtes inscrit(e) dans aucun groupe");
//                    textView.setTextColor(getResources().getColor(R.color.bleu));
//                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                    relLayout.addView(textView);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {

                    Log.i("Euro 16", "onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse)");


                    //Initialisation de la liste
                    ListView listGroupes = new ListView(getApplicationContext());
                    listGroupes.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    relLayout.addView(listGroupes);

                    adapter = new ArrayAdapterString(ChoixGroupeActivity.this, android.R.layout.simple_list_item_1);
                    listGroupes.setAdapter(adapter);

                    // Remplissage de la liste
                    hmGrpUtil = new HashMap<String, Groupe>();
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            String nomGroupe = arrayResponse.getJSONObject(i).getString("NomGrp");
                            int statutGrpUtil = Integer.parseInt(arrayResponse.getJSONObject(i).getString("Statut"));

                            if (statutGrpUtil == EUtilisateurStatut.EST_INVITE.getStatut()) {
                                lancerDemande(arrayResponse.getJSONObject(i));
                            }

                            if(statutGrpUtil == EUtilisateurStatut.PARTICIPE.getStatut()) {
                                adapter.add(nomGroupe);

                                hmGrpUtil.put(nomGroupe, new Groupe(nomGroupe, CurrentSession.utilisateur.getId(), arrayResponse.getJSONObject(i).getString("PhotoGrp")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listGroupes.setItemsCanFocus(false);
                    listGroupes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(position != 0) {
                                String nomGroupe = parent.getItemAtPosition(position).toString();
                                CurrentSession.communaute = null;
                                CurrentSession.groupe = new Groupe(nomGroupe, CurrentSession.utilisateur.getId(), hmGrpUtil.get(nomGroupe).getPhoto());
                                startActivity(new Intent(ChoixGroupeActivity.this, CompetitionActivity.class));
                            }
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
        btnCreerGroupe.setTypeface(face);
        btnCreerGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixGroupeActivity.this, CreationGroupeActivity.class));
            }
        });
    }

    private void lancerDemande(JSONObject object) {
        try {
            final JSONObject jsonObject = object;
            final String nomGroupe = jsonObject.getString("NomGrp");

            new AlertMsgBox(ChoixGroupeActivity.this, "Invitation", "Vous avez été invité(e) dans le groupe \"" + nomGroupe + "\", souhaitez-vous accepter ?", "Oui", "Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(FacebookConnexion.isOnline(ChoixGroupeActivity.this)) {
                        RestClient.updateStatutUtilisateurGroupe(nomGroupe, CurrentSession.utilisateur.getId(), EUtilisateurStatut.PARTICIPE.getStatut(), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    adapter.add(nomGroupe);
                                    hmGrpUtil.put(nomGroupe, new Groupe(nomGroupe, CurrentSession.utilisateur.getId(), jsonObject.getString("PhotoGrp")));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
                                RestClient.deleteUtilisateurGroupe(nomGroupe, CurrentSession.utilisateur.getId(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        Toast.makeText(getApplicationContext(), "Invitation rejetée", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChoixGroupeActivity.this, ChoixMondeActivity.class));
    }
}
