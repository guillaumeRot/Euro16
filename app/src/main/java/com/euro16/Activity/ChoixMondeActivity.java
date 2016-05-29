package com.euro16.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

        Button btnGlobal = (Button) findViewById(R.id.btnGlobal);
        btnGlobal.setTypeface(face);
        btnGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentSession.communaute = null;
                CurrentSession.groupe = null;
                startActivity(new Intent(ChoixMondeActivity.this, CompetitionActivity.class));
            }
        });

        Button btnCommunaute = (Button) findViewById(R.id.btnChoixCommunaute);
        btnCommunaute.setTypeface(face);
        btnCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixCommunauteActivity.class));
            }
        });

        Button btnGroupe = (Button) findViewById(R.id.btnGroupe);
        btnGroupe.setTypeface(face);
        btnGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixGroupeActivity.class));
            }
        });
    }
}
