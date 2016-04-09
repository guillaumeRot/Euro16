package com.euro16.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.EMonde;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ChoixCommunauteActivity extends AppCompatActivity {

    private boolean noItemSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_communaute);

        if(FacebookConnexion.isOnline(this)) {
            RestClient.getCommunautesUtilisateur(FacebookConnexion.profil.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    if (statusCode == 204) {
                        Log.i("Euro 16", "Vous n'êtes inscrit dans aucune communauté");
                    } else {
                        Log.i("Euro 16", "status : " + statusCode + "\n" + "response : " + arrayResponse.toString());
                        ListView listCommunautesUtil = (ListView) findViewById(R.id.listCommunautesUtilisateur);
                        final ArrayAdapter adapter = new ArrayAdapter(ChoixCommunauteActivity.this, android.R.layout.simple_list_item_single_choice);
                        listCommunautesUtil.setAdapter(adapter);
                        for (int i = 0; i < arrayResponse.length(); i++) {
                            try {
                                adapter.add(arrayResponse.getJSONObject(i).getString("NomCom"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listCommunautesUtil.setItemsCanFocus(false);
                        listCommunautesUtil.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        listCommunautesUtil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        Button btnChoixCommunaute = (Button) findViewById(R.id.btnChoisirCom);
        btnChoixCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noItemSelected) {
                    CompetitionActivity.typeCurrentMonde = EMonde.COMMUNAUTE.getTypeMonde();
                    startActivity(new Intent(ChoixCommunauteActivity.this, CompetitionActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Aucune communauté n'est séléctionné", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
