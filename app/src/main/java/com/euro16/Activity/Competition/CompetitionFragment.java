package com.euro16.Activity.Competition;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Activity.Pronostic.PronosticFragment;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Equipe;
import com.euro16.Model.Match;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.Enums.EDateFormat;
import com.euro16.Utils.Enums.EEquipeIcon;
import com.euro16.Utils.Enums.EGroupeEuro;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompetitionFragment extends Fragment {

    private FrameLayout layout;

    private ArrayList<String> nomPhases;

    private HashMap<Match, String> matchResultat;

    private TableLayout gridClassement;
    private TableLayout gridMatchs;

    private Spinner selectGroupes;

    private Typeface face;

    public static CompetitionFragment newInstance() {
        CompetitionFragment fragment = new CompetitionFragment();
        return fragment;
    }

    public CompetitionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        face = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/font_euro.ttf");

        nomPhases = new ArrayList<>();
        nomPhases.add(EGroupeEuro.GROUPE_A.getNomGrp());
        nomPhases.add(EGroupeEuro.GROUPE_B.getNomGrp());
        nomPhases.add(EGroupeEuro.GROUPE_C.getNomGrp());
        nomPhases.add(EGroupeEuro.GROUPE_D.getNomGrp());
        nomPhases.add(EGroupeEuro.GROUPE_E.getNomGrp());
        nomPhases.add(EGroupeEuro.GROUPE_F.getNomGrp());
        nomPhases.add(EGroupeEuro.HUITIEMES.getNomGrp());
        nomPhases.add(EGroupeEuro.QUARTS.getNomGrp());
        nomPhases.add(EGroupeEuro.DEMIES.getNomGrp());
        nomPhases.add(EGroupeEuro.FINALE.getNomGrp());

        // Inflate the layout for this fragment
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_competition, container, false);

        TextView tvClassement = (TextView) layout.findViewById(R.id.textViewClassement);
        tvClassement.setTypeface(face);

        TextView tvResultats = (TextView) layout.findViewById(R.id.textViewResultats);
        tvResultats.setTypeface(face);

        selectGroupes = (Spinner) layout.findViewById(R.id.selectGroupes);
        ArrayList<String> listSpinner = new ArrayList<String>();
        for(int i = 0; i < nomPhases.size(); i++) {
            listSpinner.add(nomPhases.get(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectGroupes.setAdapter(adapter);

        if(getArguments() != null) {
            String groupeArg = getArguments().getString("groupe");
            getArguments().remove("groupe");
            int spinnerSelect = adapter.getPosition(groupeArg);
            selectGroupes.setSelection(spinnerSelect);
        }


        gridClassement = (TableLayout) layout.findViewById(R.id.gridClassement);
        gridMatchs = (TableLayout) layout.findViewById(R.id.gridMatchs);

        initCompetition();

        return layout;
    }

    private void initOnSelectedItem() {
        selectGroupes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initClassement();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void initCompetition() {

        if(FacebookConnexion.isOnline(getActivity())) {
            RestClient.getMatchs(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    Toast.makeText(getActivity().getApplicationContext(), "Erreur : Aucun match n'est disponible", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    CurrentSession.groupeEquipes.clear();
                    CurrentSession.groupeMatchs.clear();
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            EGroupeEuro groupe = EGroupeEuro.getGroupeEuro(arrayResponse.getJSONObject(i).getString("Groupe"));
                            Equipe equipe1 = new Equipe(arrayResponse.getJSONObject(i).getString("Equipe1"), 0, 0, 0, 0, 0, 0);
                            Equipe equipe2 = new Equipe(arrayResponse.getJSONObject(i).getString("Equipe2"), 0, 0, 0, 0, 0, 0);
                            int score1 = !arrayResponse.getJSONObject(i).getString("Score1").equalsIgnoreCase("null") ? Integer.parseInt(arrayResponse.getJSONObject(i).getString("Score1")) : -1;
                            int score2 = !arrayResponse.getJSONObject(i).getString("Score2").equalsIgnoreCase("null") ? Integer.parseInt(arrayResponse.getJSONObject(i).getString("Score2")) : -1;

                            Timestamp timestamp = new Timestamp(Long.parseLong(arrayResponse.getJSONObject(i).getString("DateMatch") + "000"));
                            Date dateMatch = new Date(timestamp.getTime());

                            majEquipes(equipe1, equipe2, score1, score2, groupe);

                            Match match = new Match(equipe1, equipe2, score1, score2, dateMatch, groupe);
                            if (CurrentSession.getMatchs(groupe) != null) {
                                CurrentSession.getMatchs(groupe).add(match);
                            } else {
                                ArrayList<Match> matchs = new ArrayList<Match>();
                                matchs.add(match);
                                CurrentSession.groupeMatchs.put(groupe, matchs);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    initClassement();
                    initOnSelectedItem();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de récupérer les matchs", Toast.LENGTH_SHORT).show();
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

    public void majEquipes(Equipe equipe1, Equipe equipe2, int score1, int score2, EGroupeEuro groupe) {
        if(score1 != -1 && score2 != -1) {
            majEquipe(equipe1, score1 - score2, groupe);
            majEquipe(equipe2, score2 - score1, groupe);
        } else {
            majEquipe(equipe1, groupe);
            majEquipe(equipe2, groupe);
        }
    }

    public void majEquipe(Equipe equipe, EGroupeEuro groupe) {
        if(CurrentSession.getEquipeByNom(equipe.getNom()) != null) {

            equipe = CurrentSession.getEquipeByNom(equipe.getNom());

        } else {

            equipe = new Equipe(equipe.getNom(), 0, 0, 0, 0, 0, 0);
            if(CurrentSession.getEquipes(groupe) != null) {

                CurrentSession.getEquipes(groupe).add(equipe);
            } else {

                ArrayList<Equipe> alGroupe = new ArrayList<Equipe>();
                alGroupe.add(equipe);
                CurrentSession.groupeEquipes.put(groupe, alGroupe);
            }
        }
    }

    public void majEquipe(Equipe equipe, int resultatDiff, EGroupeEuro groupe) {
        if(CurrentSession.getEquipeByNom(equipe.getNom()) != null) {
            equipe = CurrentSession.getEquipeByNom(equipe.getNom());
        } else {

            equipe = new Equipe(equipe.getNom(), 0, 0, 0, 0, 0, 0);
            if(CurrentSession.getEquipes(groupe) != null) {

                CurrentSession.getEquipes(groupe).add(equipe);
            } else {

                ArrayList<Equipe> alGroupe = new ArrayList<Equipe>();
                alGroupe.add(equipe);
                CurrentSession.groupeEquipes.put(groupe, alGroupe);
            }
        }

        equipe.setJoues(equipe.getJoues()+1);
        if(resultatDiff > 0) {
            equipe.setGagnes(equipe.getGagnes()+1);
            equipe.setPts(equipe.getPts() + 3);
        } else if(resultatDiff < 0) {
            equipe.setPerdus(equipe.getPerdus() + 1);
        } else {
            equipe.setNuls(equipe.getNuls()+1);
            equipe.setPts(equipe.getPts() + 1);
        }
        equipe.setGoalAverage(equipe.getGoalAverage() + resultatDiff);
    }

    public void initClassement() {
        // Suppression des tableaux précédents
        gridClassement.removeAllViews();
        gridMatchs.removeAllViews();

        // Récupération groupe selectionné
        final EGroupeEuro groupe = EGroupeEuro.getGroupeEuro(selectGroupes.getSelectedItem().toString());

        // Initialisation classement
        if(groupe.hasClassement()) {

            View rowLayoutTitre = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.table_item_equipe, null, false);
            TextView ptsTitre = (TextView) rowLayoutTitre.findViewById(R.id.pts);
            ptsTitre.setText("Pts");
            ptsTitre.setTextColor(getResources().getColor(R.color.white));
            ptsTitre.setTypeface(face);

            TextView jouesTitre = (TextView) rowLayoutTitre.findViewById(R.id.joues);
            jouesTitre.setText("J");
            jouesTitre.setTextColor(getResources().getColor(R.color.white));
            jouesTitre.setTypeface(face);

            TextView gagnesTitre = (TextView) rowLayoutTitre.findViewById(R.id.gagnes);
            gagnesTitre.setText("G");
            gagnesTitre.setTextColor(getResources().getColor(R.color.white));
            gagnesTitre.setTypeface(face);

            TextView nulsTitre = (TextView) rowLayoutTitre.findViewById(R.id.nuls);
            nulsTitre.setText("N");
            nulsTitre.setTextColor(getResources().getColor(R.color.white));
            nulsTitre.setTypeface(face);

            TextView perdusTitre = (TextView) rowLayoutTitre.findViewById(R.id.perdus);
            perdusTitre.setText("P");
            perdusTitre.setTextColor(getResources().getColor(R.color.white));
            perdusTitre.setTypeface(face);

            TextView goalAverageTitre = (TextView) rowLayoutTitre.findViewById(R.id.goalAverage);
            goalAverageTitre.setText("+/-");
            goalAverageTitre.setTextColor(getResources().getColor(R.color.white));
            goalAverageTitre.setTypeface(face);

            gridClassement.addView(rowLayoutTitre);

            ArrayList<Equipe> equipes = CurrentSession.getEquipes(groupe);
            Collections.sort(equipes, new Comparator<Equipe>() {
                @Override
                public int compare(Equipe equipe1, Equipe equipe2) {
                    return equipe1.compareTo(equipe2);
                }
            });

            for (int i = 1; i < equipes.size()+1; i++) {

                View rowLayout = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.table_item_equipe, null, false);
                TextView classement = (TextView) rowLayout.findViewById(R.id.classement);
                classement.setText(String.valueOf(i));
                classement.setTextColor(getResources().getColor(R.color.white));
                classement.setTypeface(face);

                ImageView iconEquipe = (ImageView) rowLayout.findViewById(R.id.iconEquipe);
                String icon = EEquipeIcon.getNomIcon(equipes.get(i-1).getNom());
                if(icon != null) {
                    iconEquipe.setImageResource(getResources().getIdentifier(icon, "drawable", getActivity().getPackageName()));
                }

                TextView nom = (TextView) rowLayout.findViewById(R.id.nomEquipe);
                nom.setText(equipes.get(i-1).getNom());
                nom.setTextColor(getResources().getColor(R.color.white));
                nom.setTypeface(face);

                TextView pts = (TextView) rowLayout.findViewById(R.id.pts);
                pts.setText(String.valueOf(equipes.get(i-1).getPts()));
                pts.setTextColor(getResources().getColor(R.color.white));
                pts.setTypeface(face);

                TextView joues = (TextView) rowLayout.findViewById(R.id.joues);
                joues.setText(String.valueOf(equipes.get(i-1).getJoues()));
                joues.setTextColor(getResources().getColor(R.color.white));
                joues.setTypeface(face);

                TextView gagnes = (TextView) rowLayout.findViewById(R.id.gagnes);
                gagnes.setText(String.valueOf(equipes.get(i-1).getGagnes()));
                gagnes.setTextColor(getResources().getColor(R.color.white));
                gagnes.setTypeface(face);

                TextView nuls = (TextView) rowLayout.findViewById(R.id.nuls);
                nuls.setText(String.valueOf(equipes.get(i-1).getNuls()));
                nuls.setTextColor(getResources().getColor(R.color.white));
                gagnes.setTypeface(face);

                TextView perdus = (TextView) rowLayout.findViewById(R.id.perdus);
                perdus.setText(String.valueOf(equipes.get(i-1).getPerdus()));
                perdus.setTextColor(getResources().getColor(R.color.white));
                perdus.setTypeface(face);

                TextView goalAverage = (TextView) rowLayout.findViewById(R.id.goalAverage);
                goalAverage.setText(String.valueOf(equipes.get(i - 1).getGoalAverage()));
                goalAverage.setTextColor(getResources().getColor(R.color.white));
                goalAverage.setTypeface(face);

                gridClassement.addView(rowLayout);
            }
        } else {
            LinearLayout layoutContent = (LinearLayout) layout.findViewById(R.id.layoutContent);
            TextView tvClassement = (TextView) layoutContent.findViewById(R.id.textViewClassement);
            layoutContent.removeView(tvClassement);
        }

        if(FacebookConnexion.isOnline(getActivity())) {
            RestClient.getPronosticsUtilisateur(CurrentSession.utilisateur.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    matchResultat = new HashMap<Match, String>();
                    initMatchs(groupe, matchResultat);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    matchResultat = new HashMap<Match, String>();
                    for (int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            Equipe equipe1 = new Equipe(arrayResponse.getJSONObject(i).getString("Equipe1"), 0, 0, 0, 0, 0, 0);
                            Equipe equipe2 = new Equipe(arrayResponse.getJSONObject(i).getString("Equipe2"), 0, 0, 0, 0, 0, 0);
                            String resultat = arrayResponse.getJSONObject(i).getString("Resultat");

                            Timestamp timestamp = new Timestamp(Long.parseLong(arrayResponse.getJSONObject(i).getString("DateMatch") + "000"));
                            Date dateMatch = new Date(timestamp.getTime());

                            Match match = CurrentSession.getMatch(equipe1.getNom(), equipe2.getNom(), dateMatch);

                            matchResultat.put(match, resultat);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    initMatchs(groupe, matchResultat);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de récupérer les matchs", Toast.LENGTH_SHORT).show();
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

    public void initMatchs(EGroupeEuro groupe, HashMap<Match, String> matchResultat) {
        // Initialisation match
        for (Match match : CurrentSession.getMatchs(groupe)) {

            View rowLayout = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.table_item_match, null, false);

            ImageView iconEquipe1 = (ImageView) rowLayout.findViewById(R.id.iconEquipe1);
            String icon1 = EEquipeIcon.getNomIcon(match.getEquipe1().getNom());
            if(icon1 != null) {
                iconEquipe1.setImageResource(getResources().getIdentifier(icon1, "drawable", getActivity().getPackageName()));
            } else {
                iconEquipe1.setImageResource(getResources().getIdentifier("default_icon", "drawable", getActivity().getPackageName()));
            }

            TextView nomEquipe1 = (TextView) rowLayout.findViewById(R.id.nomEquipe1);
            nomEquipe1.setText(match.getEquipe1().getNom());
            nomEquipe1.setTextColor(getResources().getColor(R.color.white));
            nomEquipe1.setTypeface(face);

            TextView nomEquipe2 = (TextView) rowLayout.findViewById(R.id.nomEquipe2);
            nomEquipe2.setText(match.getEquipe2().getNom());
            nomEquipe2.setTextColor(getResources().getColor(R.color.white));
            nomEquipe2.setTypeface(face);

            if(match.getScore1() != -1 && match.getScore2() != -1) {
                TextView score1 = (TextView) rowLayout.findViewById(R.id.score1);
                score1.setText(String.valueOf(match.getScore1()));
                score1.setTextColor(getResources().getColor(R.color.white));
                score1.setTypeface(face);

                TextView score2 = (TextView) rowLayout.findViewById(R.id.score2);
                score2.setText(String.valueOf(match.getScore2()));
                score2.setTextColor(getResources().getColor(R.color.white));
                score2.setTypeface(face);

                int resultMatch = match.getScore1() - match.getScore2();
                if(matchResultat.get(match) != null) {
                    if((resultMatch > 0 && matchResultat.get(match).equalsIgnoreCase("1"))
                            ||(resultMatch < 0 && matchResultat.get(match).equalsIgnoreCase("2"))
                            || (resultMatch == 0 && matchResultat.get(match).equalsIgnoreCase("N"))
                            || (resultMatch == 0 && matchResultat.get(match).equalsIgnoreCase("n"))) {
                        rowLayout.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                } else {
                    rowLayout.setBackgroundColor(getResources().getColor(R.color.rouge));
                }

                if(resultMatch > 0) {
                    nomEquipe1.setTypeface(face, Typeface.BOLD);
                } else if(resultMatch < 0) {
                    nomEquipe2.setTypeface(face, Typeface.BOLD);
                }

            }

            TextView prono = (TextView) rowLayout.findViewById(R.id.pronoUtil);
            if(matchResultat.get(match) != null) {
                prono.setText("Pronostic : " + matchResultat.get(match));
            }
            prono.setTextColor(getResources().getColor(R.color.white));
            prono.setTypeface(face);

            ImageView iconEquipe2 = (ImageView) rowLayout.findViewById(R.id.iconEquipe2);
            String icon2 = EEquipeIcon.getNomIcon(match.getEquipe2().getNom());
            if(icon2 != null) {
                iconEquipe2.setImageResource(getResources().getIdentifier(icon2, "drawable", getActivity().getPackageName()));
            } else {
                iconEquipe2.setImageResource(getResources().getIdentifier("default_icon", "drawable", getActivity().getPackageName()));
            }

            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 10, 0, 10);
            rowLayout.setLayoutParams(layoutParams);

            rowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String equipe1 = ((TextView) v.findViewById(R.id.nomEquipe1)).getText().toString();
                    String equipe2 = ((TextView) v.findViewById(R.id.nomEquipe2)).getText().toString();
                    EGroupeEuro groupe = EGroupeEuro.getGroupeEuro(selectGroupes.getSelectedItem().toString());
                    Match match = CurrentSession.getMatch(equipe1, equipe2, groupe);

                    if (match.getDateMatch().after(new Date())) {
                        if (FacebookConnexion.isOnline(getActivity())) {

                            SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_GET_MATCH.getFormatDate());
                            RestClient.getMatch(match.getEquipe1().getNom(), match.getEquipe2().getNom(), dateFormat.format(match.getDateMatch()), new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {

                                    Match match = null;
                                    try {
                                        if (CurrentSession.getEquipeByNom(jsonObject.getString("Equipe1")) != null
                                                && CurrentSession.getEquipeByNom(jsonObject.getString("Equipe2")) != null) {

                                            Equipe equipe1 = CurrentSession.getEquipeByNom(jsonObject.getString("Equipe1"));
                                            Equipe equipe2 = CurrentSession.getEquipeByNom(jsonObject.getString("Equipe2"));
                                            int score1 = !jsonObject.getString("Score1").equalsIgnoreCase("null") ? Integer.parseInt(jsonObject.getString("Score1")) : -1;
                                            int score2 = !jsonObject.getString("Score2").equalsIgnoreCase("null") ? Integer.parseInt(jsonObject.getString("Score2")) : -1;

                                            Timestamp timestamp = new Timestamp(Long.parseLong(jsonObject.getString("DateMatch") + "000"));
                                            Date dateMatch = new Date(timestamp.getTime());

                                            EGroupeEuro groupe = EGroupeEuro.getGroupeEuro(jsonObject.getString("Groupe"));

                                            match = new Match(equipe1, equipe2, score1, score2, dateMatch, groupe);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (match != null) {
                                        PronosticFragment pronoFragment = new PronosticFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("match", match);
                                        bundle.putBoolean("callFromCompetition", true);
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

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de récupèrer les informations de ce match", Toast.LENGTH_SHORT).show();
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
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "La date pour pronostiquer ce match est dépassée", Toast.LENGTH_LONG).show();
                    }
                }
            });
            gridMatchs.addView(rowLayout);
        }
    }
}
