package com.euro16.Activity.Communaute;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.ChoixMondeActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.Communaute;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.ETypeCommunaute;
import com.euro16.Utils.Enums.EUtilisateurStatut;
import com.euro16.Utils.ListsView.ArrayAdapterString;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class RejoindreCommunauteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT < 21) {
            setTheme(R.style.AppTheme);
            getSupportActionBar().setSubtitle(R.string.title_activity_rejoindre_communaute);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejoindre_communaute);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        if(Build.VERSION.SDK_INT >= 21) {
            TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
            title.setText(R.string.title_activity_rejoindre_communaute);
            title.setTypeface(face);
        }

        final ListView listCommunautes = (ListView) findViewById(R.id.listCommunautes);
        final ArrayAdapterString adapter = new ArrayAdapterString(RejoindreCommunauteActivity.this, android.R.layout.simple_list_item_1);

        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.setTypeface(face);

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
            RestClient.getCommunautes(CurrentSession.utilisateur.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(final int statusCode, Header[] headers, JSONArray arrayResponse) {
                    if (statusCode == 204) {
                        Toast.makeText(RejoindreCommunauteActivity.this, "Aucune communauté n'est disponible", Toast.LENGTH_LONG).show();
                    } else {
                        final HashMap<String, Communaute> hmCommunautes = new HashMap<String, Communaute>();
                        listCommunautes.setAdapter(adapter);
                        for (int i = 0; i < arrayResponse.length(); i++) {
                            try {
                                adapter.add(arrayResponse.getJSONObject(i).getString("NomCom"));
                                hmCommunautes.put(arrayResponse.getJSONObject(i).getString("NomCom"), new Communaute(arrayResponse.getJSONObject(i).getString("NomCom"), CurrentSession.utilisateur.getId(), arrayResponse.getJSONObject(i).getString("PhotoCom"), arrayResponse.getJSONObject(i).getString("TypeCom")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listCommunautes.setItemsCanFocus(false);
                        listCommunautes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final int pos = position;
                                final String typeCom = hmCommunautes.get(parent.getItemAtPosition(position)).getType();
                                String textConfirmation = "Êtes-vous sûr de vouloir rejoindre la communauté \"" + parent.getItemAtPosition(position) + "\"?";
                                if(typeCom.equalsIgnoreCase(ETypeCommunaute.PRIVEE.getTypeBase())) {
                                    textConfirmation += "\nIl vous faudra attendre la validation par l'administrateur avant de pouvoir y participer.";
                                }
                                new AlertMsgBox(RejoindreCommunauteActivity.this, "Confirmation", textConfirmation, "Oui", "Non", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ajouterUtilisateurCommunaute(adapter.getItem(pos).toString(), typeCom);
                                        startActivity(new Intent(RejoindreCommunauteActivity.this, ChoixCommunauteActivity.class));
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }
                                );
                            }
                        });
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), "Impossible de récupérer les communautés", Toast.LENGTH_SHORT).show();
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
    }

    private void ajouterUtilisateurCommunaute(String nomCommunaute, String typeCom) {
        int statut = 0;
        if(typeCom.equalsIgnoreCase(ETypeCommunaute.PUBLIQUE.getTypeBase())) {
            statut = EUtilisateurStatut.PARTICIPE.getStatut();
        } else if(typeCom.equalsIgnoreCase(ETypeCommunaute.PRIVEE.getTypeBase())) {
            statut = EUtilisateurStatut.DEMANDE_PARTICIPE.getStatut();
        }
        Log.i("Euro 16", "statut utilisateur dans la comm : " + statut);
        if(statut != 0) {
            RestClient.ajouterUtilisateurCommunaute(CurrentSession.utilisateur.getId(), nomCommunaute, statut, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i("Euro 16", "statut : " + statusCode);
                    startActivity(new Intent(RejoindreCommunauteActivity.this, ChoixCommunauteActivity.class));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("Euro 16", "Impossible de demander l'ajout à la communauté : " + statusCode);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RejoindreCommunauteActivity.this, ChoixCommunauteActivity.class));
    }
}
