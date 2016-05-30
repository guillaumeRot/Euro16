package com.euro16.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.euro16.Activity.Communaute.ChoixCommunauteActivity;
import com.euro16.Activity.Competition.CompetitionActivity;
import com.euro16.Activity.Groupe.ChoixGroupeActivity;
import com.euro16.Model.CurrentSession;
import com.euro16.R;

public class ChoixMondeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT < 21) {
            setTheme(R.style.AppTheme);
            getSupportActionBar().setSubtitle(R.string.title_choix_monde);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_monde);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity);

        if(Build.VERSION.SDK_INT >= 21) {
            TextView title = (TextView) toolbar.findViewById(R.id.title_toolbar);
            title.setText(R.string.title_choix_monde);
            title.setTypeface(face);
        }

        TextView labelGlobal = (TextView) findViewById(R.id.btnGlobal);
        labelGlobal.setTypeface(face);

        TextView labelCommunaute = (TextView) findViewById(R.id.btnChoixCommunaute);
        labelCommunaute.setTypeface(face);

        TextView labelGroupe = (TextView) findViewById(R.id.btnGroupe);
        labelGroupe.setTypeface(face);

        LinearLayout btnGlobal = (LinearLayout) findViewById(R.id.layout_choix_global);
        btnGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentSession.communaute = null;
                CurrentSession.groupe = null;
                startActivity(new Intent(ChoixMondeActivity.this, CompetitionActivity.class));
            }
        });

        LinearLayout btnCommunaute = (LinearLayout) findViewById(R.id.layout_choix_communaute);
        btnCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixCommunauteActivity.class));
            }
        });

        LinearLayout btnGroupe = (LinearLayout) findViewById(R.id.layout_choix_groupe);
        btnGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixGroupeActivity.class));
            }
        });
    }
}
