package com.euro16.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.euro16.R;

public class ChoixMondeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_monde);

        getSupportActionBar().setSubtitle(R.string.title_choix_monde);

        Button btnGlobal = (Button) findViewById(R.id.button2);
        btnGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, MainPageActivity.class));
            }
        });

        Button btnCommunaute = (Button) findViewById(R.id.button3);
        btnCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, MainPageActivity.class));
            }
        });

        Button btnGroupe = (Button) findViewById(R.id.button4);
        btnGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoixMondeActivity.this, MainPageActivity.class));
            }
        });
    }
}
