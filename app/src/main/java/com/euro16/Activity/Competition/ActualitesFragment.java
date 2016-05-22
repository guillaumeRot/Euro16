package com.euro16.Activity.Competition;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.euro16.Model.Utilisateur;
import com.euro16.R;
import com.euro16.Utils.Enums.EUtilisateurStatut;
import com.euro16.Utils.ListsView.ListViewAdapterActualite;
import com.euro16.Utils.ListsView.ListViewAdapterUtilisateur;
import com.euro16.Utils.RowsChoix.RowActualite;
import com.euro16.Utils.RowsChoix.RowChoixUtilisateur;
import com.euro16.Utils.XMLDataParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActualitesFragment extends Fragment {

    private FrameLayout layout;
    private RelativeLayout relLayout;
    private ListViewAdapterActualite adapter;

    public ActualitesFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout) inflater.inflate(R.layout.fragment_actualites, container, false);

        relLayout = (RelativeLayout) layout.findViewById(R.id.relLayoutActus);

        initListView();
        getXMLData();

        return layout;
    }

    public void initListView() {
        ListView listActus = new ListView(getActivity().getApplicationContext());
        listActus.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        relLayout.addView(listActus);

        adapter = new ListViewAdapterActualite(getActivity(), R.layout.list_actualite);
        listActus.setAdapter(adapter);
    }

    public void getXMLData() {

        ArrayList<RowActualite> actus = new ArrayList<>();
        try {
            actus = new XMLDataParser().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(RowActualite actu : actus) {
            adapter.add(actu);
            adapter.notifyDataSetChanged();
        }
    }
}
