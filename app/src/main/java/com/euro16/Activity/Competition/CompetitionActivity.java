package com.euro16.Activity.Competition;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.ChoixMondeActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Activity.Parametres.GererMondeFragment;
import com.euro16.Activity.Parametres.ParametresFragment;
import com.euro16.Activity.Pronostic.PronosticFragment;
import com.euro16.Model.CurrentSession;
import com.euro16.Model.Equipe;
import com.euro16.Model.Match;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.euro16.Utils.CustomTypefaceSpan;
import com.euro16.Utils.Enums.EGroupeEuro;
import com.euro16.Utils.ListsView.ListViewAdapterClassement;
import com.euro16.Utils.RowsChoix.RowClassementUtilisateur;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class CompetitionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationViewLeft;
    private NavigationView classementView;

    private ListViewAdapterClassement adapter;

    private boolean pronoMenuActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT < 21) {
            setTheme(R.style.AppTheme);
            getSupportActionBar().setSubtitle(R.string.title_choix_monde);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        if(Build.VERSION.SDK_INT >= 21) {
            TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
            title.setText("Compétition");
            title.setTypeface(face);

            TextView subtitle = (TextView) toolbar.findViewById(R.id.subtitle_toolbar);
            if(CurrentSession.communaute != null) {
                if(CurrentSession.communaute.getNom().length() <= 8) {
                    subtitle.setText(getResources().getString(R.string.title_activity_left_menu) + " : " + CurrentSession.communaute.getNom());
                } else {
                    subtitle.setText(getResources().getString(R.string.title_activity_left_menu) + " : " + CurrentSession.communaute.getNom().substring(0, 14) + "...");
                }
            } else if(CurrentSession.groupe != null) {
                if(CurrentSession.groupe.getNom().length() <= 8) {
                    subtitle.setText(getResources().getString(R.string.title_activity_left_menu) + " : " + CurrentSession.groupe.getNom());
                } else {
                    subtitle.setText(getResources().getString(R.string.title_activity_left_menu) + " : " + CurrentSession.groupe.getNom().substring(0, 14) + "...");
                }
            } else {
                subtitle.setText(getResources().getString(R.string.title_activity_left_menu) + " : Global");
            }
            subtitle.setTypeface(face);
        }

        ImageView imageRightMenu = (ImageView) toolbar.findViewById(R.id.imageRightMenu);
        imageRightMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.END);
            }
        });

        pronoMenuActivated = false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        navigationViewLeft = (NavigationView) findViewById(R.id.nav_view_left);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        Menu m = navigationViewLeft.getMenu();
        for (int i = 0;i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            applyFontToMenuItem(mi);
        }

        isAdmin();

        classementView = (NavigationView) findViewById(R.id.nav_view_right);
        classementView.setNavigationItemSelectedListener(this);

        TextView tvClassement = (TextView) classementView.findViewById(R.id.textViewClassement);
        tvClassement.setTypeface(face);

        TextView tvNbJoueurs = (TextView) classementView.findViewById(R.id.nbJoueursClassement);
        tvNbJoueurs.setTypeface(face);

        initMatchsNonPronostiques();
        initClassement();

        FragmentManager fragmentManager = getFragmentManager();
        Fragment frag = null;
        try {
            frag = CompetitionFragment.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(frag != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.view_container, frag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_right_menu) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.END);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        Fragment frag = null;
        if (id == R.id.nav_competition) {

            try {
                frag = CompetitionFragment.class.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_pronostics) {

            if (!CurrentSession.matchNonPronostiques.isEmpty()) {
                PronosticFragment pronoFragment = new PronosticFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("match", CurrentSession.matchNonPronostiques.getFirst());
                bundle.putBoolean("callFromCompetition", false);
                pronoFragment.setArguments(bundle);

                getFragmentManager().beginTransaction()
                        .replace(R.id.mainLayout, pronoFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(CompetitionActivity.this, "Vous avez déjà pronostiquer sur tous les matchs", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_actualites) {

            try {
                frag = ActualitesFragment.class.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_gerer_monde) {

            try {
                frag = GererMondeFragment.class.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_parametres) {

            try {
                frag = ParametresFragment.class.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_change_monde) {
            startActivity(new Intent(CompetitionActivity.this, ChoixMondeActivity.class));
        }

        if(frag != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.view_container, frag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void isAdmin() {
        if(FacebookConnexion.isOnline(CompetitionActivity.this)) {
            if(CurrentSession.groupe != null) {
                RestClient.getGroupe(CurrentSession.groupe.getNom(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (CurrentSession.utilisateur.getId().equalsIgnoreCase(response.getString("AdminGrp"))) {
                                navigationViewLeft.getMenu().findItem(R.id.nav_gerer_monde).setTitle("Gérer le groupe");
                                applyFontToMenuItem(navigationViewLeft.getMenu().findItem(R.id.nav_gerer_monde));
                            } else {
                                navigationViewLeft.getMenu().removeItem(R.id.nav_gerer_monde);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Impossible de récupérer les informations du groupe", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(CurrentSession.communaute != null) {
                RestClient.getCommunaute(CurrentSession.communaute.getNom(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if(CurrentSession.utilisateur.getId().equalsIgnoreCase(response.getString("AdminCom"))) {
                                navigationViewLeft.getMenu().findItem(R.id.nav_gerer_monde).setTitle("Gérer la communauté");
                                applyFontToMenuItem(navigationViewLeft.getMenu().findItem(R.id.nav_gerer_monde));
                            } else {
                                navigationViewLeft.getMenu().removeItem(R.id.nav_gerer_monde);
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Impossible de récupérer les informations de la communauté", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                navigationViewLeft.getMenu().removeItem(R.id.nav_gerer_monde);
            }
        } else {
            new AlertMsgBox(CompetitionActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }

    public void initClassement() {
        if(CurrentSession.communaute != null) {
            if(FacebookConnexion.isOnline(CompetitionActivity.this)) {
                    RestClient.getClassementCommunaute(CurrentSession.communaute.getNom(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                            remplirClassement(arrayResponse);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Impossible de récupérer le classement de la communauté", Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                new AlertMsgBox(CompetitionActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        } else if(CurrentSession.groupe != null) {
            if(FacebookConnexion.isOnline(CompetitionActivity.this)) {
                RestClient.getClassementGroupe(CurrentSession.groupe.getNom(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                        remplirClassement(arrayResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Impossible de récupérer le classement du groupe", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                new AlertMsgBox(CompetitionActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        } else {
            if(FacebookConnexion.isOnline(CompetitionActivity.this)) {
                RestClient.getClassementGlobal(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                        remplirClassement(arrayResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Impossible de récupérer le classement global", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                new AlertMsgBox(CompetitionActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        }
    }

    public void remplirClassement(JSONArray arrayResponse) {

        ListView listClassement = (ListView) classementView.findViewById(R.id.list_classement);
        adapter = new ListViewAdapterClassement(CompetitionActivity.this, R.layout.list_item_communaute);
        listClassement.setAdapter(adapter);

        int nbJoueurs = 0;

        for (int i = 0; i < arrayResponse.length(); i++) {
            try {
                String nomUti = arrayResponse.getJSONObject(i).getString("NomUti");
                String prenomUti = arrayResponse.getJSONObject(i).getString("PrenomUti");
                String photoUti = arrayResponse.getJSONObject(i).getString("PhotoUti");
                String ptsUti = arrayResponse.getJSONObject(i).getString("Points");
                String idFacebook = arrayResponse.getJSONObject(i).getString("ID_Facebook");

                if(i < 6 || idFacebook.equalsIgnoreCase(CurrentSession.utilisateur.getId()) || i == arrayResponse.length()-1) {
                    RowClassementUtilisateur row = new RowClassementUtilisateur(String.valueOf(i+1), idFacebook, nomUti, prenomUti, photoUti, ptsUti);
                    adapter.add(row);
                } else if(i == 6 || i == arrayResponse.length()-2) {
                    RowClassementUtilisateur row = new RowClassementUtilisateur("", "", "...", "", "", "");
                    adapter.add(row);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nbJoueurs++;
        }

        TextView nbJoueursClassement = (TextView) classementView.findViewById(R.id.nbJoueursClassement);
        nbJoueursClassement.setText(nbJoueurs + " joueur(s)");

        listClassement.setItemsCanFocus(false);
    }

    public void initMatchsNonPronostiques() {
        if(FacebookConnexion.isOnline(CompetitionActivity.this)) {
            RestClient.getMatchsNonPronostiques(CurrentSession.utilisateur.getId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray arrayResponse) {
                    for(int i = 0; i < arrayResponse.length(); i++) {
                        try {
                            Equipe equipe1 = new Equipe(arrayResponse.getJSONObject(i).getString("Equipe1"));
                            Equipe equipe2 = new Equipe(arrayResponse.getJSONObject(i).getString("Equipe2"));

                            Timestamp timestamp = new Timestamp(Long.parseLong(arrayResponse.getJSONObject(i).getString("DateMatch") + "000"));
                            Date dateMatch = new Date(timestamp.getTime());

                            EGroupeEuro groupe = EGroupeEuro.getGroupeEuro(arrayResponse.getJSONObject(i).getString("Groupe"));

                            CurrentSession.matchNonPronostiques.add(new Match(equipe1, equipe2, -1, -1, dateMatch, groupe));

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        if(i == arrayResponse.length() - 1) {
                            pronoMenuActivated = true;
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), "Impossible de récupérer les matchs non pronostiqués", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            new AlertMsgBox(CompetitionActivity.this, getResources().getString(R.string.title_msg_box), getResources().getString(R.string.body_msg_box), getResources().getString(R.string.button_msg_box), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan(getApplicationContext(), "" , face), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}
