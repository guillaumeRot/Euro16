package com.euro16.Activity.Pronostic;


import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.Facebook.FacebookConnexion;
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
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class PronosticFragment extends Fragment {

    private FrameLayout layout;

    private Match match;

    public PronosticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_pronostic, container, false);

        match = getArguments().getParcelable("match");

        Log.i("Euro 16", "THE SUPER MATCH : " + match.toString());

        initDate(match.getDateMatch());
        initEquipes(match.getEquipe1(), match.getEquipe2());
        initListeners(match.getEquipe1(), match.getEquipe2(), match.getDateMatch());

        return layout;
    }

    public void initDate(Date dateMatch) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_PRONOSTIC.getFormatDate());
        TextView tvDate = (TextView) layout.findViewById(R.id.tvDateTimeProno);
        tvDate.setText(dateFormat.format(dateMatch));
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
//        if(FacebookConnexion.isOnline(getActivity())) {
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_GET_MATCH.getFormatDate());
//            RestClient.getPronostic(CurrentSession.utilisateur.getId(), equipe1.getNom(), equipe2.getNom(), dateFormat.format(dateMatch), new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Success : Aucun match n'est disponible", Toast.LENGTH_SHORT).show();
//                    Log.i("Euro 16", "response : " + jsonObject);
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Success : le match est dispo", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de récupérer les matchs : " + statusCode, Toast.LENGTH_SHORT).show();
//                    Log.i("Euro 16", "response failure 2 : " + responseString);
//                }
//            });
//        } else {
//            new AlertMsgBox(getActivity(), getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    getActivity().finish();
//                }
//            });
//        }


        Button btnProno1 = (Button) layout.findViewById(R.id.choix1Prono);
        btnProno1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FacebookConnexion.isOnline(getActivity())) {

                    SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATETIME_BASE.getFormatDate());
                    RestClient.creerPronostic(CurrentSession.utilisateur.getId(), match.getEquipe1().getNom(), match.getEquipe2().getNom(), dateFormat.format(match.getDateMatch()), String.valueOf("1"), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                            Toast.makeText(getActivity().getApplicationContext(), "Success : Prono créé", Toast.LENGTH_SHORT).show();
                            Log.i("Euro 16", "response : " + jsonObject);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getActivity().getApplicationContext(), "Erreur : Impossible de créér le pronostic : " + statusCode, Toast.LENGTH_SHORT).show();
                            Log.i("Euro 16", "response failure 2 : " + responseString);
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
