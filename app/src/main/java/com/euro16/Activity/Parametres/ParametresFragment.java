package com.euro16.Activity.Parametres;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONException;

public class ParametresFragment extends Fragment {

    private FrameLayout layout;

    public static ParametresFragment newInstance() {
        ParametresFragment fragment = new ParametresFragment();
        return fragment;
    }

    public ParametresFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_parametres, container, false);

        Typeface face = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/font_euro.ttf");

        TextView mentionsLegales = (TextView) layout.findViewById(R.id.mentionsLegales);

        mentionsLegales.setText("Les images et le flux RSS utilisées dans cette application sont la propriété de l'UEFA (http://fr.uefa.com). \n" +
                "\n" +
                "Application développée par :\n" +
                "Pierre-Hugues LAUNE \n" +
                "Guillaume ROT\n" +
                "\n" +
                "2016 - Centrale Lille - IG2I\n" +
                "\n" +
                "\n" +
                "Vous pouvez vous désinscrire à tout moment en cliquant sur le bouton \"Désinscription\" ci-dessous.");
        mentionsLegales.setTypeface(face);

        Button btnDesinscrire = (Button) layout.findViewById(R.id.btnDesinscrire);
        btnDesinscrire.setTypeface(face);
        btnDesinscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FacebookConnexion.isOnline(getActivity())) {
                    new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            boolean isSuccess = false;
                            try {
                                isSuccess = response.getJSONObject().getBoolean("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (isSuccess && response.getError()==null){
                                deconnexion();
                            }

                        }
                    }).executeAsync();
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

        Button btnDeconnexion = (Button) layout.findViewById(R.id.btnDeconnexion);
        btnDeconnexion.setTypeface(face);
        btnDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deconnexion();
            }
        });

        return layout;
    }

    public void deconnexion() {
        LoginManager.getInstance().logOut();
        CurrentSession.utilisateur = null;
        startActivity(new Intent(getActivity(), FacebookConnexion.class));
    }

}
