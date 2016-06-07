package com.euro16.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.euro16.Activity.Facebook.FacebookConnexion;
import com.euro16.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/font_euro.ttf");

        TextView tvPresentation = (TextView) findViewById(R.id.tvPresentation);
        tvPresentation.setTypeface(face);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, FacebookConnexion.class));
            }
        }, 3000);
    }
}
