package com.euro16.Utils.ListsView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.euro16.Model.CurrentSession;
import com.euro16.R;
import com.euro16.Utils.Enums.EDateFormat;
import com.euro16.Utils.RowsChoix.RowActualite;
import com.euro16.Utils.RowsChoix.RowClassementUtilisateur;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Guillaume on 20/05/2016.
 */
public class ListViewAdapterActualite extends ArrayAdapter<RowActualite> {

    Context context;

    public ListViewAdapterActualite(Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
    }

    private class ViewHolder {
        TextView textViewDate;
        TextView textViewTitle;
        TextView textViewDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowActualite rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_actualites, null);
            holder = new ViewHolder();
            holder.textViewDate = (TextView) convertView.findViewById(R.id.datePub);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.titleActu);
            holder.textViewDesc = (TextView) convertView.findViewById(R.id.descActu);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        DateFormat dateFormatParse = new SimpleDateFormat(EDateFormat.DATETIME_PARSE_ACTU.getFormatDate(), Locale.ENGLISH);
        DateFormat dateFormatDisplay = new SimpleDateFormat(EDateFormat.DATE_SIMPLE.getFormatDate());
        Date datePub = new Date();
        try {
            datePub = dateFormatParse.parse(rowItem.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.textViewDate.setText(dateFormatDisplay.format(datePub));
        holder.textViewTitle.setText(rowItem.getTitle());
        holder.textViewDesc.setText(rowItem.getDescription());

        return convertView;
    }
}
