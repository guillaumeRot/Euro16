package com.euro16.Activity.Competition;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.euro16.R;
import com.euro16.Utils.ListsView.ListViewAdapterActualite;
import com.euro16.Utils.RowsChoix.RowActualite;
import com.euro16.Utils.XMLDataParser;

import java.util.ArrayList;

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

        adapter = new ListViewAdapterActualite(getActivity(), R.layout.list_actualites);
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
