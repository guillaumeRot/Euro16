package com.euro16.Utils.ListsView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.euro16.R;
import com.euro16.Utils.RowsChoix.RowChoixUtilisateur;
import com.squareup.picasso.Picasso;

/**
 * Created by Guillaume on 16/04/2016.
 */
public class ListViewAdapterUtilisateur extends ArrayAdapter<RowChoixUtilisateur> {

    Context context;

    public ListViewAdapterUtilisateur(Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
    }

    private class ViewHolder {
        TextView textViewNom;
        TextView textViewPrenom;
        ImageView ImageViewPhoto;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowChoixUtilisateur rowItem = getItem(position);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/font_euro.ttf");

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_utilisateur, null);
            holder = new ViewHolder();
            holder.textViewNom = (TextView) convertView.findViewById(R.id.listNomUtilisateur);
            holder.textViewPrenom = (TextView) convertView.findViewById(R.id.listPrenomUtilisateur);
            holder.ImageViewPhoto = (ImageView) convertView.findViewById(R.id.statutUtilImage);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNom.setText(rowItem.getNom());
        holder.textViewNom.setTypeface(face);

        holder.textViewPrenom.setText(rowItem.getPrenom());
        holder.textViewPrenom.setTypeface(face);

        String photoUrl = rowItem.getPhoto().replace("http", "https");
        Picasso.with(context.getApplicationContext()).load(photoUrl).resize(150, 150).into(holder.ImageViewPhoto);

        convertView.setBackgroundColor(context.getApplicationContext().getResources().getColor(R.color.white_opacity));

        return convertView;
    }
}
