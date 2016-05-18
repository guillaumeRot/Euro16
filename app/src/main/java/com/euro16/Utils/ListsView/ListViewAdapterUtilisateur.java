package com.euro16.Utils.ListsView;

import android.app.Activity;
import android.content.Context;
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
        holder.textViewPrenom.setText(rowItem.getPrenom());
        Picasso.with(context.getApplicationContext()).load("https://scontent.xx.fbcdn.net/v/t1.0-1/p200x200/10953929_10206199409086769_5590917736953600821_n.jpg?oh=aaea76c587fac5612e87b09f78ab66fe&oe=57A3256E").into(holder.ImageViewPhoto);

        return convertView;
    }
}
