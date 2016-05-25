package com.euro16.Activity.Tutoriel;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.euro16.Activity.ChoixMondeActivity;
import com.euro16.R;
import com.viewpagerindicator.CirclePageIndicator;

public class TutorielActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoriel);

        getSupportActionBar().setSubtitle(R.string.title_activity_tutoriel);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        CirclePageIndicator circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        circleIndicator.setViewPager(mViewPager);

        Button btnPasser = (Button) findViewById(R.id.btnPasserTuto);
        btnPasser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorielActivity.this, ChoixMondeActivity.class));
            }
        });
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutoriel, container, false);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return SelectMondeFragment.newInstance();
            } else if(position == 1){
                return CompetitionFragment.newInstance();
            } else if(position == 2){
                return PronosticFragment.newInstance();
            } else if(position == 3){
                return ClassementFragment.newInstance();
            } else if(position == 4){
                return ActualitesFragment.newInstance();
            } else if(position == 5){
                return SuperVictorFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
                case 4:
                    return "SECTION 5";
                case 5:
                    return "SECTION 6";
            }
            return null;
        }
    }

    public static class SelectMondeFragment extends Fragment {

        public static SelectMondeFragment newInstance() {
            SelectMondeFragment f = new SelectMondeFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_tutoriel, container, false);

            TextView descTuto = (TextView) layout.findViewById(R.id.descTuto);
            descTuto.setText("Jouez dans différents mondes : Avec tous les joueurs, dans une communauté, ou entre amis.");

            return layout;
        }
    }

    public static class CompetitionFragment extends Fragment {

        public static CompetitionFragment newInstance() {
            CompetitionFragment f = new CompetitionFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_tutoriel, container, false);

            TextView descTuto = (TextView) layout.findViewById(R.id.descTuto);
            descTuto.setText("Retrouvez le récapitulatif de la compétition.");

            return layout;
        }
    }

    public static class PronosticFragment extends Fragment {

        public static PronosticFragment newInstance() {
            PronosticFragment f = new PronosticFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_tutoriel, container, false);

            TextView descTuto = (TextView) layout.findViewById(R.id.descTuto);
            descTuto.setText("Pronostiquez sur les différents matchs et prouvez que vous êtes le meilleur !");

            return layout;
        }
    }

    public static class ClassementFragment extends Fragment {

        public static ClassementFragment newInstance() {
            ClassementFragment f = new ClassementFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_tutoriel, container, false);

            TextView descTuto = (TextView) layout.findViewById(R.id.descTuto);
            descTuto.setText("Suivez votre évolution à l’aide du classement.");

            return layout;
        }
    }

    public static class ActualitesFragment extends Fragment {

        public static ActualitesFragment newInstance() {
            ActualitesFragment f = new ActualitesFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_tutoriel, container, false);

            TextView descTuto = (TextView) layout.findViewById(R.id.descTuto);
            descTuto.setText("Affinez vos pronostics avec les actualités de l’UEFA Euro 2016.");

            return layout;
        }
    }

    public static class SuperVictorFragment extends Fragment {

        public static SuperVictorFragment newInstance() {
            SuperVictorFragment f = new SuperVictorFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_tutoriel, container, false);

            TextView descTuto = (TextView) layout.findViewById(R.id.descTuto);
            descTuto.setText("SUPER VICTOR !!!");

            return layout;
        }
    }
}
