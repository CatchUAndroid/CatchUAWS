package com.uren.catchu.Adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uren.catchu.R;

public class CustomListAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    Context mContext;
    int resource;
    List<String> optionsList;

    private class ViewHolder {
        TextView txtItem;
    }

    public CustomListAdapter(Context context, int resource, List<String> list) {
        super(context, resource, list);

        this.mContext = context;
        this.resource = resource;
        this.optionsList = list;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();
            view = inflater.inflate(resource, null);

            holder.txtItem = (TextView) view.findViewById(R.id.txtItem);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txtItem.setText(optionsList.get(position));
        if (optionsList.get(position).equals(mContext.getResources().getString(R.string.delete))) {
            holder.txtItem.setTextColor(mContext.getResources().getColor(R.color.red, null));
        } else if (optionsList.get(position).equals(mContext.getResources().getString(R.string.unfollow))) {
            holder.txtItem.setTextColor(mContext.getResources().getColor(R.color.red, null));
        } else {
            holder.txtItem.setTextColor(mContext.getResources().getColor(R.color.black, null));
        }

        return view;
    }

}