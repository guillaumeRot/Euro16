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
import com.euro16.Utils.RowsChoix.RowChoixCommunaute;

/**
 * Created by Guillaume on 16/04/2016.
 */
public class ListViewAdapterCommunaute extends ArrayAdapter<RowChoixCommunaute> {

    Context context;

    public ListViewAdapterCommunaute(Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
    }

    private class ViewHolder {
        TextView textViewNom;
        ImageView statutUtilImage;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowChoixCommunaute rowItem = getItem(position);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/font_euro.ttf");

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_communaute, null);
            holder = new ViewHolder();
            holder.textViewNom = (TextView) convertView.findViewById(R.id.listNomCommunaute);
            holder.statutUtilImage = (ImageView) convertView.findViewById(R.id.statutUtilImage);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNom.setText(rowItem.getNom());
        holder.textViewNom.setTypeface(face);
        //if(rowItem.getStatutUtilImage() == EUtilisateurStatut.PARTICIPE.getStatut()) {
            holder.textViewNom.setTextColor(context.getResources().getColor(R.color.white));
//        } else {
//            holder.textViewNom.setTextColor(context.getResources().getColor(R.color.grey));
//        }

        holder.statutUtilImage.setImageResource(rowItem.getStatutUtilImage());

        return convertView;
    }
}
