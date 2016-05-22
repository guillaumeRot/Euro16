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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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

        Log.i("Euro 16", "match prono : " + match + " : " + EEquipeIcon.getNomIcon(match.getEquipe1().getNom()) + " : " + EEquipeIcon.getNomIcon(match.getEquipe1().getNom()));
        if(match != null && (EEquipeIcon.getNomIcon(match.getEquipe1().getNom()) != null && EEquipeIcon.getNomIcon(match.getEquipe2().getNom()) != null)) {
            initDate();
            initGroupe();
            initEquipes();
            initPronosticsJoueurs();
            initListeners();
        } else {
            goBackCompetition(match.getGroupe());
        }

        return layout;
    }

    public void initDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_PRONOSTIC.getFormatDate());
        TextView tvDate = (TextView) layout.findViewById(R.id.tvDateTimeProno);
        tvDate.setText(dateFormat.format(match.getDateMatch()));
    }

    public void initGroupe() {
        TextView tvDate = (TextView) layout.findViewById(R.id.tvGroupeProno);
        tvDate.setText(match.getGroupe().getNomGrp());
    }

    public void initEquipes() {
        ImageView iconEquipe1 = (ImageView) layout.findViewById(R.id.icon1Prono);
        String strIcon1 = EEquipeIcon.getNomIcon(match.getEquipe1().getNom());
        if(strIcon1 != null) {
            iconEquipe1.setImageResource(getResources().getIdentifier(strIcon1, "drawable", getActivity().getPackageName()));
        }

        ImageView iconEquipe2 = (ImageView) layout.findViewById(R.id.icon2Prono);
        String strIcon2 = EEquipeIcon.getNomIcon(match.getEquipe2().getNom());
        if(strIcon2 != null) {
            iconEquipe2.setImageResource(getResources().getIdentifier(strIcon2, "drawable", getActivity().getPackageName()));
        }

        TextView nomEquipe1 = (TextView) layout.findViewById(R.id.nom1Prono);
        nomEquipe1.setText(match.getEquipe1().getNom());

        TextView nomEquipe2 = (TextView) layout.findViewById(R.id.nom2Prono);
        nomEquipe2.setText(match.getEquipe2().getNom());
    }

    public void initCotes() {
        // A remplir avec API Parions Sport
    }

    public void initPronosticsJoueurs() {
        DateFormat format = new SimpleDateFormat(EDateFormat.DATETIME_GET_MATCH.getFormatDate());
        if(FacebookConnexion.isOnline(getActivity())) {
            if(CurrentSession.communaute != null) {
                RestClient.getPronosticsCommunaute(CurrentSession.communaute.getNom(), match.getEquipe1().getNom(), match.getEquipe2().getNom(), format.format(match.getDateMatch()), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                        calcPronosJoueurs(arrayResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de récupérer les pronostics des autres joueurs existants concernant le match", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(CurrentSession.groupe != null) {
                RestClient.getPronosticsGroupe(CurrentSession.groupe.getNom(), match.getEquipe1().getNom(), match.getEquipe2().getNom(), format.format(match.getDateMatch()), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                        calcPronosJoueurs(arrayResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de récupérer les pronostics des autres joueurs existants concernant le match", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                RestClient.getPronosticsGlobal(match.getEquipe1().getNom(), match.getEquipe2().getNom(), format.format(match.getDateMatch()), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                        calcPronosJoueurs(arrayResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de récupérer les pronostics des autres joueurs existants concernant le match", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            new AlertMsgBox(getActivity(), getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
        }
    }

    public void initListeners() {

        DateFormat format = new SimpleDateFormat(EDateFormat.DATETIME_GET_MATCH.getFormatDate());
        if(FacebookConnexion.isOnline(getActivity())) {
            RestClient.getPronostic(CurrentSession.utilisateur.getId(), match.getEquipe1().getNom(), match.getEquipe2().getNom(), format.format(match.getDateMatch()), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    if(jsonObject.length() == 0) {
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
                    } else {
                        Button btnProno1 = (Button) layout.findViewById(R.id.choix1Prono);
                        btnProno1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePronosticListener("1");
                            }
                        });

                        Button btnProno2 = (Button) layout.findViewById(R.id.choix2Prono);
                        btnProno2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePronosticListener("2");
                            }
                        });

                        Button btnPronoN = (Button) layout.findViewById(R.id.choixNProno);
                        btnPronoN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePronosticListener("N");
                            }
                        });
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de récupérer les pronostics existants concernant le match", Toast.LENGTH_SHORT).show();
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

    public void creerPronosticListener(String resultat) {
        if(FacebookConnexion.isOnline(getActivity())) {

            SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_GET_MATCH.getFormatDate());
            RestClient.creerPronostic(CurrentSession.utilisateur.getId(), match.getEquipe1().getNom(), match.getEquipe2().getNom(), dateFormat.format(match.getDateMatch()), resultat, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getActivity().getApplicationContext(), "Le pronostic a été créé", Toast.LENGTH_SHORT).show();

                    if (!callFromCompetition) {
                        CurrentSession.matchNonPronostiques.removeFirst();
                    }

                    if (callFromCompetition || CurrentSession.matchNonPronostiques.isEmpty()) {
                        Log.i("Euro 16", "match non prono remove : " + CurrentSession.getMatchNonProno(match.getEquipe1().getNom(), match.getEquipe2().getNom(), match.getDateMatch()));
                        CurrentSession.matchNonPronostiques.remove(CurrentSession.getMatchNonProno(match.getEquipe1().getNom(), match.getEquipe2().getNom(), match.getDateMatch()));

                        goBackCompetition(match.getGroupe());
                    } else {
                        if (CurrentSession.matchNonPronostiques.getFirst() != null) {
                            PronosticFragment pronoFragment = new PronosticFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("match", CurrentSession.matchNonPronostiques.getFirst());
                            pronoFragment.setArguments(bundle);

                            getFragmentManager().beginTransaction()
                                    .replace(R.id.mainLayout, pronoFragment)
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
    }

    public void updatePronosticListener(String resultat) {
        if(FacebookConnexion.isOnline(getActivity())) {

            SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_GET_MATCH.getFormatDate());
            RestClient.updatePronostic(CurrentSession.utilisateur.getId(), match.getEquipe1().getNom(), match.getEquipe2().getNom(), dateFormat.format(match.getDateMatch()), null, null, resultat, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getActivity().getApplicationContext(), "Le pronostic a été modifié", Toast.LENGTH_SHORT).show();

                    goBackCompetition(match.getGroupe());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de modifié le pronostic", Toast.LENGTH_SHORT).show();
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

    public void goBackCompetition(EGroupeEuro groupe) {
        CompetitionFragment competFragment = new CompetitionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("groupe", groupe.getNomGrp());
        competFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, competFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void calcPronosJoueurs(JSONArray arrayResponse) {
        int nbChoix1 =0 , nbChoixN = 0, nbChoix2 = 0;
        for(int i = 0; i < arrayResponse.length(); i++) {
            try {
                String resultatJoueurs = arrayResponse.getJSONObject(i).getString("Resultat");

                if(resultatJoueurs.equalsIgnoreCase("1")){
                    nbChoix1++;
                } else if(resultatJoueurs.equalsIgnoreCase("N")) {
                    nbChoixN++;
                } else if(resultatJoueurs.equalsIgnoreCase("2")) {
                    nbChoix2++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int nbChoixTotal = nbChoix1 + nbChoixN + nbChoix2;

        double pourcChoix1 = (double) nbChoix1 / nbChoixTotal * 100;
        double pourcChoixN = (double) nbChoixN / nbChoixTotal * 100;
        double pourcChoix2 = (double) nbChoix2 / nbChoixTotal * 100;

        TextView pronoChoix1Joueurs = (TextView) layout.findViewById(R.id.pronoEquipe1Joueurs);
        pronoChoix1Joueurs.setText((int) Math.round(pourcChoix1) + "%");

        TextView pronoChoixNJoueurs = (TextView) layout.findViewById(R.id.pronoNulJoueurs);
        pronoChoixNJoueurs.setText((int) Math.round(pourcChoixN) + "%");

        TextView pronoChoix2Joueurs = (TextView) layout.findViewById(R.id.pronoEquipe2Joueurs);
        pronoChoix2Joueurs.setText((int) Math.round(pourcChoix2) + "%");
    }
}
