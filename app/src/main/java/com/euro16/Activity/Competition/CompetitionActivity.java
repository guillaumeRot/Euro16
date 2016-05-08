package com.euro16.Activity.Competition;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.euro16.API.RestClient;
import com.euro16.Activity.ChoixMondeActivity;
import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.Activity.Parametres.GererMondeFragment;
import com.euro16.Activity.Parametres.ParametresFragment;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.AlertMsgBox;
import com.facebook.login.LoginManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CompetitionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationViewLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*if(CurrentSession.communaute != null) {
            Log.i("Euro 16", "Communaute : " + CurrentSession.communaute.toString());
        }
        if(CurrentSession.groupe != null) {
            Log.i("Euro 16", "Groupe : " + CurrentSession.groupe.toString());
        }
        if(CurrentSession.communaute == null && CurrentSession.groupe == null) {
            Log.i("Euro 16", "Mode GLOBAL");
        }*/

        if(CurrentSession.communaute != null) {
            if(CurrentSession.communaute.getNom().length() <= 16) {
                toolbar.setSubtitle(getResources().getString(R.string.title_activity_left_menu) + " : " + CurrentSession.communaute.getNom());
            } else {
                toolbar.setSubtitle(getResources().getString(R.string.title_activity_left_menu) + " : " + CurrentSession.communaute.getNom().substring(0, 14) + "...");
            }
        } else if(CurrentSession.groupe != null) {
            if(CurrentSession.groupe.getNom().length() <= 16) {
                toolbar.setSubtitle(getResources().getString(R.string.title_activity_left_menu) + " : " + CurrentSession.groupe.getNom());
            } else {
                toolbar.setSubtitle(getResources().getString(R.string.title_activity_left_menu) + " : " + CurrentSession.groupe.getNom().substring(0, 14) + "...");
            }
        } else {
            toolbar.setSubtitle(getResources().getString(R.string.title_activity_left_menu) + " : Global");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        navigationViewLeft = (NavigationView) findViewById(R.id.nav_view_left);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        isAdmin();

        NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
        navigationViewRight.setNavigationItemSelectedListener(this);

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
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

        } else if (id == R.id.nav_actualites) {

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
        } else if(id == R.id.nav_deconnexion) {
            LoginManager.getInstance().logOut();
            CurrentSession.utilisateur = null;
            startActivity(new Intent(CompetitionActivity.this, FacebookConnexion.class));
        }

        if(frag != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.view_container, frag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
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
}
