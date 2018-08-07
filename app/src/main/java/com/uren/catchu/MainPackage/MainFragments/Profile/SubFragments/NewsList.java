package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

public class NewsList extends Fragment {

    RecyclerView rvNewsList;

    public NewsList() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rvNewsList = (RecyclerView) inflater.inflate(R.layout.news_recycler_view, container, false);
        return rvNewsList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        List<String> content = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            content.add("content " + i);
        }
        rvNewsList.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        rvNewsList.setLayoutManager(lm);
        MySimpleAdapter adapter = new MySimpleAdapter(content);
        rvNewsList.setAdapter(adapter);
    }

    private class MySimpleAdapter extends RecyclerView.Adapter {

        List<String> content = new ArrayList<>();

        public MySimpleAdapter(List<String> c) {
            content.addAll(c);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder vh = (MyViewHolder) holder;
            vh.tv.setText("Option " + position);
        }

        @Override
        public int getItemCount() {
            return content.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tvRv);
            }
        }
    }
}