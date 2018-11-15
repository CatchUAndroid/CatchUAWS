package com.uren.catchu.MainPackage.MainFragments.Profile.UserShareManagement.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uren.catchu.R;

import java.util.List;


public class UserCatchedPostAdapter extends RecyclerView.Adapter<UserCatchedPostAdapter.MyViewHolder> {

    private Context context;
    private List<String> textList;

    public UserCatchedPostAdapter(Context context, List<String> textList) {
        this.context = context;
        this.textList = textList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        String text;
        TextView tvRv;
        int position;

        public MyViewHolder(View view) {
            super(view);

            tvRv = (TextView) view.findViewById(R.id.tvRv);

        }

        public void setData(String text, int position) {

            this.text = text;
            this.position = position;

            //Load data
            this.tvRv.setText(text);
        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String text = textList.get(position);
        holder.setData(text, position);
    }

    @Override
    public int getItemCount() {
        return textList.size();
    }

    public void updateAdapterWithPosition(int position) {

        notifyItemChanged(position);
    }


}


