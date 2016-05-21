package com.euro16.Activity.Parametres;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Utilisateur;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.EUtilisateurStatut;
import com.euro16.Utils.ListsView.ListViewAdapterUtilisateur;
import com.euro16.Utils.RowsChoix.RowChoixUtilisateur;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;


public class GererMondeFragment extends Fragment {

    private RelativeLayout relLayout;
    private FrameLayout layout;

    public static GererMondeFragment newInstance() {
        GererMondeFragment fragment = new GererMondeFragment();
        return fragment;
    }

    public GererMondeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_gerer_monde, container, false);

        TextView title = (TextView) layout.findViewById(R.id.titleGestionMonde);
        if(CurrentSession.groupe != null) {
            title.setText("Gestion du groupe");
        } else if(CurrentSession.communaute != null) {
            title.setText("Gestion de la communauté");
        }

        if(FacebookConnexion.isOnline(getActivity())) {
            getDemandes();
        } else {
            new AlertMsgBox(getActivity(), getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
        }

        Button btnInviter = (Button) layout.findViewById(R.id.btnInviter);
        btnInviter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), InviteFriendsActivity.class));
            }
        });

        return layout;
    }

    private void getDemandes() {
        if(CurrentSession.communaute != null) {
            relLayout = (RelativeLayout) layout.findViewById(R.id.relLayout);

            RestClient.getUtilisateursCommunaute(CurrentSession.communaute.getNom(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    Log.i("Euro 16", "statusCode : " + statusCode + " : " + arrayResponse);

                    ListView listDemandes = new ListView(getActivity().getApplicationContext());
                    listDemandes.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    TextView textHeader = new TextView(getActivity().getApplicationContext());
                    textHeader.setText("Vos demandes :");
                    listDemandes.addHeaderView(textHeader);
                    relLayout.addView(listDemandes);

                    final ListViewAdapterUtilisateur adapter = new ListViewAdapterUtilisateur(getActivity(), R.layout.list_item_utilisateur);
                    listDemandes.setAdapter(adapter);

                    final HashMap<String, Utilisateur> hmNomUtil = new HashMap<String, Utilisateur>();
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            String nom = arrayResponse.getJSONObject(i).getString("NomUti");
                            String prenom = arrayResponse.getJSONObject(i).getString("PrenomUti");
                            String photo = arrayResponse.getJSONObject(i).getString("PhotoUti");
                            int statutCommUtil = Integer.parseInt(arrayResponse.getJSONObject(i).getString("Statut"));

                            if(statutCommUtil == EUtilisateurStatut.DEMANDE_PARTICIPE.getStatut()) {

                                RowChoixUtilisateur row = new RowChoixUtilisateur(nom, prenom, photo);
                                adapter.add(row);

                                hmNomUtil.put(nom, new Utilisateur(arrayResponse.getJSONObject(i).getString("ID_Facebook"), nom, prenom, arrayResponse.getJSONObject(i).getString("Email"), photo));
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
                                new AlertMsgBox(getActivity(), "Demande", "Voulez-vous accepter la demande de " + hmNomUtil.get(nom).getPrenom().toString() + " " + nom.toUpperCase() + " pour rejoindre la communauté \"" + CurrentSession.communaute.getNom() + "\"?", "Oui", "Non",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (FacebookConnexion.isOnline(getActivity())) {
                                                    RestClient.updateStatutUtilisateurCommunaute(CurrentSession.communaute.getNom(), hmNomUtil.get(nom).getId(), EUtilisateurStatut.PARTICIPE.getStatut(), new AsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            adapter.remove(adapter.getItem(position - 1));
                                                            if (adapter.getCount() == 0) {
                                                                displayTextNoUser();
                                                            }
                                                            Toast.makeText(getActivity().getApplicationContext(), "Demande acceptée", Toast.LENGTH_SHORT).show();
                                                            // L'utilisateur est autorisé à jouer dans cette communauté
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                            Log.i("Euro 16", "oui : failure : " + statusCode + " : " + error.getStackTrace());
                                                            // Il y a eu un problème lors de la confirmation d'invitation
                                                        }
                                                    });
                                                } else {
                                                    new AlertMsgBox(getActivity(), getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            getActivity().finish();
                                                        }
                                                    });
                                                }
                                            }
                                        },
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (FacebookConnexion.isOnline(getActivity())) {
                                                    RestClient.deleteUtilisateurCommunaute(CurrentSession.communaute.getNom(), hmNomUtil.get(nom).getId(), new AsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            adapter.remove(adapter.getItem(position - 1));
                                                            if (adapter.getCount() == 0) {
                                                                displayTextNoUser();
                                                            }
                                                            Toast.makeText(getActivity().getApplicationContext(), "Demande rejetée", Toast.LENGTH_SHORT).show();
                                                            // L'utilisateur a bien été supprimé de la communauté
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                            Log.i("Euro 16", "non : onFailure : " + statusCode);
                                                            // Il y a eu un problème lors de la suppression d'invitation
                                                        }
                                                    });
                                                } else {
                                                    new AlertMsgBox(getActivity(), getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            getActivity().finish();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("Euro 16", "les utilisateurs de la communauté, statut : " + statusCode);
                }
            });
        }
    }

    private void displayTextNoUser() {
        relLayout.removeAllViews();
        TextView textView = new TextView(getActivity().getApplicationContext());
        textView.setText("Vous n'avez pas de demandes en attente");
        textView.setTextColor(getResources().getColor(R.color.bleu));
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        relLayout.addView(textView);
    }
}
