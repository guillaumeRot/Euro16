package com.euro16.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.euro16.Activity.Communaute.ChoixCommunauteActivity;
import com.euro16.Activity.Groupe.ChoixGroupeActivity;
import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.EMonde;

public class ChoixMondeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_monde);

        getSupportActionBar().setSubtitle(R.string.title_choix_monde);

        Button btnGlobal = (Button) findViewById(R.id.btnGlobal);
        btnGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentSession.communaute = null;
                CurrentSession.groupe = null;
                startActivity(new Intent(ChoixMondeActivity.this, CompetitionActivity.class));
            }
        });

        Button btnCommunaute = (Button) findViewById(R.id.btnChoixCommunaute);
        btnCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixCommunauteActivity.class));
            }
        });

        Button btnGroupe = (Button) findViewById(R.id.btnGroupe);
        btnGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, ChoixGroupeActivity.class));
            }
        });
    }
}
