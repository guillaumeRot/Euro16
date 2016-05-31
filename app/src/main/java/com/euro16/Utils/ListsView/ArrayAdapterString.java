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

/**
 * Created by Guillaume on 31/05/2016.
 */
public class ArrayAdapterString extends ArrayAdapter<String> {

    Context context;

    public ArrayAdapterString(Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
    }

    private class ViewHolder {
        TextView textViewNom;
        ImageView statutUtilImage;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        String item = getItem(position);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/font_euro.ttf");

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
            holder = new ViewHolder();
            holder.textViewNom = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNom.setText(item);
        holder.textViewNom.setTypeface(face);
        holder.textViewNom.setTextColor(context.getResources().getColor(R.color.white));

        return convertView;
    }
}
