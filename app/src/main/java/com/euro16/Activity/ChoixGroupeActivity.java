package com.euro16.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.EMonde;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class ChoixGroupeActivity extends AppCompatActivity {

    private boolean noItemSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_groupe);

        final ListView listGroupes = (ListView) findViewById(R.id.listGroupes);
        Button btnChoisirGrp = (Button) findViewById(R.id.btnRejoindreGroupe);

        if(FacebookConnexion.isOnline(this)) {
            RestClient.getGroupesUtilisateur(FacebookConnexion.profil.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    if (statusCode == 204) {
                        Log.i("Euro 16", "Vous n'êtes inscrit dans aucun groupe");
                    } else {
                        Log.i("Euro 16", "status : " + statusCode + "\n" + "response : " + arrayResponse.toString());
                        final ArrayAdapter adapter = new ArrayAdapter(ChoixGroupeActivity.this, android.R.layout.simple_list_item_single_choice);
                        listGroupes.setAdapter(adapter);
                        for (int i = 0; i < arrayResponse.length(); i++) {
                            try {
                                adapter.add(arrayResponse.getJSONObject(i).getString("NomGrp"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listGroupes.setItemsCanFocus(false);
                        listGroupes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        listGroupes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                noItemSelected = false;
                                CompetitionActivity.nomCurrentMonde = adapter.getItem(position).toString();
                            }
                        });
                    }
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

        btnChoisirGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noItemSelected) {
                    CompetitionActivity.typeCurrentMonde = EMonde.GROUPE.getTypeMonde();
                    startActivity(new Intent(ChoixGroupeActivity.this, CompetitionActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Aucun groupe n'est séléctionné", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnCreerGroupe = (Button) findViewById(R.id.btnCreerGroupe);
        btnCreerGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixGroupeActivity.this, CreationGroupeActivity.class));
            }
        });
    }
}
