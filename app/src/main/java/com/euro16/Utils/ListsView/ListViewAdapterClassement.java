package com.euro16.Utils.ListsView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.euro16.R;
import com.euro16.Utils.ImageFromUrl;
import com.euro16.Utils.RowsChoix.RowChoixUtilisateur;
import com.euro16.Utils.RowsChoix.RowClassementUtilisateur;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Guillaume on 12/05/2016.
 */
public class ListViewAdapterClassement extends ArrayAdapter<RowClassementUtilisateur> {

    Context context;

    public ListViewAdapterClassement(Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
    }

    private class ViewHolder {
        TextView textViewNom;
        TextView textViewPrenom;
        ImageView ImageViewPhoto;
        TextView textViewPts;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowClassementUtilisateur rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_utilisateur_classement, null);
            holder = new ViewHolder();
            holder.textViewNom = (TextView) convertView.findViewById(R.id.listNomUtilisateur);
            holder.textViewPrenom = (TextView) convertView.findViewById(R.id.listPrenomUtilisateur);
            holder.ImageViewPhoto = (ImageView) convertView.findViewById(R.id.statutUtilImage);
            holder.textViewPts = (TextView) convertView.findViewById(R.id.listPtsUtilisateur);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNom.setText(rowItem.getNom());
        holder.textViewPrenom.setText(rowItem.getPrenom());

        Log.i("Euro 16", "photo : " + rowItem.getPhoto());


        new ImageFromUrl(holder.ImageViewPhoto).execute(rowItem.getPhoto());
        holder.textViewPts.setText(rowItem.getPts() + "pts");

        return convertView;
    }
}
