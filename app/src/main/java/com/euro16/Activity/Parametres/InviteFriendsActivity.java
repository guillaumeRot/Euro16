package com.euro16.Activity.Parametres;

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
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Activity.SplashScreenActivity;
import com.euro16.Config;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Utilisateur;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.EUtilisateurStatut;
import com.euro16.Utils.ListsView.ListViewAdapterUtilisateur;
import com.euro16.Utils.RowsChoix.RowChoixUtilisateur;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class InviteFriendsActivity extends AppCompatActivity {

    private RelativeLayout relLayout;
    private ArrayList<String> usersMonde;

    private CallbackManager callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        getSupportActionBar().setSubtitle(R.string.title_activity_invite_amis);

        relLayout = (RelativeLayout) findViewById(R.id.relLayout);

        if(FacebookConnexion.isOnline(this)) {
            getFriends();
        } else {
            new AlertMsgBox(InviteFriendsActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }

        Button btnInviteFb = (Button) findViewById(R.id.btnInviteFb);
        btnInviteFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogInvite();
            }
        });
    }

    private void getFriends() {
        GraphRequest friendsRequest = GraphRequest.newMyFriendsRequest(SplashScreenActivity.accessToken, new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                ArrayList<Utilisateur> friends = new ArrayList<Utilisateur>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        String email = "no email";
                        if (jsonArray.getJSONObject(i).has("email")) {
                            email = jsonArray.getJSONObject(i).getString("email");
                        }
                        friends.add(new Utilisateur(jsonArray.getJSONObject(i).getString("id"), jsonArray.getJSONObject(i).getString("last_name"), jsonArray.getJSONObject(i).getString("first_name"), email, "http://graph.facebook.com/" + jsonArray.getJSONObject(i).getString("id") + "/picture?type=large"));
                        Log.i("Euro 16", jsonArray.getJSONObject(i).getString("id") + " : " + jsonArray.getJSONObject(i).getString("last_name") + " : " + jsonArray.getJSONObject(i).getString("first_name") + " : " + email + " : " + "http://graph.facebook.com/" + jsonArray.getJSONObject(i).getString("id") + "/picture?type=large");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                remplirList(friends);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name, email");
        friendsRequest.setParameters(parameters);
        friendsRequest.executeAsync();
    }

    private void remplirList(final ArrayList<Utilisateur> users) {
        // Récupération des utilisateurs du monde
        usersMonde = new ArrayList<String>();
        if(CurrentSession.communaute != null) {
            Log.i("Euro 16", "comm");
            RestClient.getUtilisateursCommunaute(CurrentSession.communaute.getNom(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            usersMonde.add(arrayResponse.getJSONObject(i).getString("ID_Facebook"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    initList(users);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("Euro 16", "Impossible de récupérer les utilisateurs de la communauté, statut : " + statusCode);
                }
            });
        } else if(CurrentSession.groupe != null) {
            RestClient.getUtilisateursGroupe(CurrentSession.groupe.getNom(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            usersMonde.add(arrayResponse.getJSONObject(i).getString("ID_Facebook"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    initList(users);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("Euro 16", "Impossible de récupérer les utilisateurs de la communauté, statut : " + statusCode);
                }
            });
        }
    }

    private void initList(ArrayList<Utilisateur> users) {
        // Initialisation de la liste
        ListView listDemandes = new ListView(getApplicationContext());
        listDemandes.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView textHeader = new TextView(getApplicationContext());
        textHeader.setText("Vos amis :");
        listDemandes.addHeaderView(textHeader);
        relLayout.addView(listDemandes);

        final ListViewAdapterUtilisateur adapter = new ListViewAdapterUtilisateur(InviteFriendsActivity.this, R.layout.list_item_utilisateur);
        listDemandes.setAdapter(adapter);

        final HashMap<String, Utilisateur> hmNomUtil = new HashMap<String, Utilisateur>();
        for (Utilisateur user : users) {
            try {
                String nom = user.getNom();
                String prenom = user.getPrenom();
                String photo = user.getPhoto();

                if(!usersMonde.contains(user.getId())) {

                    RowChoixUtilisateur row = new RowChoixUtilisateur(nom, prenom, photo);
                    adapter.add(row);

                    hmNomUtil.put(nom, new Utilisateur(user.getId(), nom, prenom, user.getEmail(), photo));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(adapter.getCount() == 0) {
            displayTextNoUser();
        }
        listDemandes.setItemsCanFocus(false);
        listDemandes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                if(position != 0) {
                    final String nom = adapter.getItem(position - 1).getNom();
                    if (CurrentSession.communaute != null) {
                        new AlertMsgBox(InviteFriendsActivity.this, "Invitation", "Voulez-vous inviter " + hmNomUtil.get(nom).getPrenom().toString() + " " + nom.toUpperCase() + " à rejoindre la communauté \"" + CurrentSession.communaute.getNom() + "\"?", "Oui", "Non",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (FacebookConnexion.isOnline(InviteFriendsActivity.this)) {
                                            RestClient.ajouterUtilisateurCommunaute(hmNomUtil.get(nom).getId(), CurrentSession.communaute.getNom(), EUtilisateurStatut.EST_INVITE.getStatut(), new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    Log.i("Euro 16", "oui : success : " + statusCode);
                                                    adapter.remove(adapter.getItem(position - 1));
                                                    if (adapter.getCount() == 0) {
                                                        displayTextNoUser();
                                                    }
                                                    Toast.makeText(getApplicationContext(), "Invitation envoyée", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.i("Euro 16", "oui : failure : " + statusCode + " : " + error.getStackTrace());
                                                }
                                            });
                                        } else {
                                            new AlertMsgBox(InviteFriendsActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
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
                                    }
                                });
                    } else if (CurrentSession.groupe != null) {
                        new AlertMsgBox(InviteFriendsActivity.this, "Invitation", "Voulez-vous inviter " + hmNomUtil.get(nom).getPrenom().toString() + " " + nom.toUpperCase() + " à rejoindre le groupe \"" + CurrentSession.groupe.getNom() + "\"?", "Oui", "Non",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (FacebookConnexion.isOnline(InviteFriendsActivity.this)) {
                                            Log.i("Euro 16", "test : " + CurrentSession.groupe.getNom() + " : " + hmNomUtil.get(nom).getId());
                                            RestClient.ajouterUtilisateurGroupe(hmNomUtil.get(nom).getId(), CurrentSession.groupe.getNom(), EUtilisateurStatut.EST_INVITE.getStatut(), new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    adapter.remove(adapter.getItem(position - 1));
                                                    if (adapter.getCount() == 0) {
                                                        displayTextNoUser();
                                                    }
                                                    Toast.makeText(getApplicationContext(), "Invitation acceptée", Toast.LENGTH_SHORT).show();
                                                    // L'utilisateur est autorisé à jouer dans cette communauté
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Log.i("Euro 16", "oui : failure : " + statusCode + " : " + error.getStackTrace());
                                                    // Il y a eu un problème lors de la confirmation d'invitation
                                                }
                                            });
                                        } else {
                                            new AlertMsgBox(InviteFriendsActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
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
                                    }
                                });
                    }
                }
            }
        });
    }

    private void displayTextNoUser() {
        relLayout.removeAllViews();
        TextView textView = new TextView(getApplicationContext());
        if(CurrentSession.communaute != null) {
            textView.setText("Tous vos amis jouent déjà dans cette communauté");
        } else if(CurrentSession.groupe != null) {
            textView.setText("Tous vos amis jouent déjà dans ce groupe");
        }
        textView.setTextColor(getResources().getColor(R.color.bleu));
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        relLayout.addView(textView);
    }

    public void openDialogInvite() {
        String AppUri = "https://fb.me/" + Config.appLinkUrl;

        String previewImageUrl = "https://www.facebook.com/photo.php?fbid=10205339178821550&set=t.1545550276&type=3&theater";

        callback = CallbackManager.Factory.create();

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(AppUri).setPreviewImageUrl(previewImageUrl)
                    .build();

            AppInviteDialog appInviteDialog = new AppInviteDialog(InviteFriendsActivity.this);
            Log.i("Euro 16", "registration callback");
            appInviteDialog.registerCallback(callback, new FacebookCallback<AppInviteDialog.Result>() {
                @Override
                public void onSuccess(AppInviteDialog.Result result) {
                    // L'invitation est bien envoyé
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException e) {
                }
            });

            appInviteDialog.show(content);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callback.onActivityResult(requestCode, resultCode, data);
    }
}
