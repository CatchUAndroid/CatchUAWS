package com.uren.catchu.MainPackage.MainFragments.Feed;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.Adapters.FeedAdapter;
import com.uren.catchu.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FeedFragment extends BaseFragment {

    View mView;
    private LinearLayoutManager layoutManager;
    FeedAdapter feedAdapter;

    @BindView(R.id.feed_recyclerView)
    RecyclerView feed_recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            mView = inflater.inflate(R.layout.fragment_feed, container, false);
            ButterKnife.bind(this, mView);

            init();
            setUpRecyclerView();

        return mView;
    }

    private void init() {

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    }

    private void setUpRecyclerView() {

        ArrayList<Integer> list = new ArrayList<Integer>();

        for(int i=0; i< 15; i++){
            list.add(i+1);
        }


        feedAdapter = new FeedAdapter(getActivity(), list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        feed_recyclerView.setLayoutManager(mLayoutManager);
        feed_recyclerView.setAdapter(feedAdapter);



    }


}
