package com.euro16.Activity.Pronostic;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.Competition.CompetitionFragment;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Equipe;
import com.euro16.Model.Match;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.EDateFormat;
import com.euro16.Utils.Enums.EEquipeIcon;
import com.euro16.Utils.Enums.EGroupeEuro;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class PronosticFragment extends Fragment {

    private FrameLayout layout;

    private Match match;

    private boolean callFromCompetition;

    public PronosticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_pronostic, container, false);

        match = getArguments().getParcelable("match");
        getArguments().remove("match");

        callFromCompetition = getArguments().getBoolean("callFromCompetition");
        getArguments().remove("callFromCompetition");

        initDate(match.getDateMatch());
        initGroupe(match.getGroupe());
        initEquipes(match.getEquipe1(), match.getEquipe2());
        initListeners(match.getEquipe1(), match.getEquipe2(), match.getDateMatch());

        return layout;
    }

    public void initDate(Date dateMatch) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_PRONOSTIC.getFormatDate());
        TextView tvDate = (TextView) layout.findViewById(R.id.tvDateTimeProno);
        tvDate.setText(dateFormat.format(dateMatch));
    }

    public void initGroupe(EGroupeEuro groupe) {
        TextView tvDate = (TextView) layout.findViewById(R.id.tvGroupeProno);
        tvDate.setText(groupe.getNomGrp());
    }

    public void initEquipes(Equipe equipe1, Equipe equipe2) {
        ImageView iconEquipe1 = (ImageView) layout.findViewById(R.id.icon1Prono);
        String strIcon1 = EEquipeIcon.getNomIcon(equipe1.getNom());
        if(strIcon1 != null) {
            iconEquipe1.setImageResource(getResources().getIdentifier(strIcon1, "drawable", getActivity().getPackageName()));
        }

        ImageView iconEquipe2 = (ImageView) layout.findViewById(R.id.icon2Prono);
        String strIcon2 = EEquipeIcon.getNomIcon(equipe2.getNom());
        if(strIcon2 != null) {
            iconEquipe2.setImageResource(getResources().getIdentifier(strIcon2, "drawable", getActivity().getPackageName()));
        }

        TextView nomEquipe1 = (TextView) layout.findViewById(R.id.nom1Prono);
        nomEquipe1.setText(equipe1.getNom());

        TextView nomEquipe2 = (TextView) layout.findViewById(R.id.nom2Prono);
        nomEquipe2.setText(equipe2.getNom());
    }

    public void initCotes() {
        // A remplir avec API Parions Sport
    }

    public void initListeners(Equipe equipe1, Equipe equipe2, Date dateMatch) {



        Button btnProno1 = (Button) layout.findViewById(R.id.choix1Prono);
        btnProno1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerPronosticListener("1");
            }
        });

        Button btnProno2 = (Button) layout.findViewById(R.id.choix2Prono);
        btnProno2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerPronosticListener("2");
            }
        });

        Button btnPronoN = (Button) layout.findViewById(R.id.choixNProno);
        btnPronoN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerPronosticListener("N");
            }
        });
    }

    public void creerPronosticListener(String resultat) {
        //if(match.getDateMatch().after(new Date())) {
            if(FacebookConnexion.isOnline(getActivity())) {

                SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_GET_MATCH.getFormatDate());
                RestClient.creerPronostic(CurrentSession.utilisateur.getId(), match.getEquipe1().getNom(), match.getEquipe2().getNom(), dateFormat.format(match.getDateMatch()), resultat, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getActivity().getApplicationContext(), "Le pronostic a été créé", Toast.LENGTH_SHORT).show();

                        if(!callFromCompetition) {
                            CurrentSession.matchNonPronostiques.removeFirst();
                        }

                        Log.i("Euro 16", "callFromCompetition : " + callFromCompetition + " : " + CurrentSession.matchNonPronostiques);
                        if(callFromCompetition || CurrentSession.matchNonPronostiques.isEmpty()) {
                            CurrentSession.matchNonPronostiques.remove(match);
                            FragmentManager fragmentManager = getFragmentManager();
                            Fragment frag = null;
                            try {
                                frag = CompetitionFragment.class.newInstance();
                            } catch (java.lang.InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            fragmentManager.beginTransaction()
                                    .replace(R.id.view_container, frag)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                        } else {
                            if (CurrentSession.matchNonPronostiques.getFirst() != null) {
                                PronosticFragment pronoFragment = new PronosticFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("match", CurrentSession.matchNonPronostiques.getFirst());
                                pronoFragment.setArguments(bundle);

                                getFragmentManager().beginTransaction()
                                        .replace(R.id.layoutCompetition, pronoFragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Impossible de récupérer les informations de ce match", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de créér le pronostic", Toast.LENGTH_SHORT).show();
                        Log.i("Euro 16", "impossible de créer le pronostic : " + statusCode);
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
//        } else {
//            Toast.makeText(getActivity().getApplicationContext(), "La date pour pronostiquer le match est dépassée", Toast.LENGTH_LONG).show();
//        }
    }
}
