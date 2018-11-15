package com.uren.catchu.MainPackage.MainFragments.Profile.UserShareManagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.UserShareManagement.Adapters.UserCatchedPostAdapter;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCatchedPostFragment extends BaseFragment{


    View mView;

    @BindView(R.id.backImgv)
    ImageView backImgv;
    @BindView(R.id.changeViewTv)
    TextView changeViewTv;
    @BindView(R.id.nextImgv)
    ImageView nextImgv;
    @BindView(R.id.shareRecyclerView)
    RecyclerView postCatchedRecyclerView;
    @BindView(R.id.changeViewLayout)
    LinearLayout changeViewLayout;

    boolean listViewSelected = false;

    public UserCatchedPostFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_user_catched_post_layout, container, false);
            ButterKnife.bind(this, mView);
            addListeners();
            setInitVariables();
            setUpPager();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void addListeners(){
        changeViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!listViewSelected){
                    listViewSelected = true;
                    backImgv.setVisibility(View.VISIBLE);
                    nextImgv.setVisibility(View.GONE);
                    changeViewTv.setText(getResources().getString(R.string.SHOW_IN_NORMAL_MODE));

                }else {
                    listViewSelected = false;
                    backImgv.setVisibility(View.GONE);
                    nextImgv.setVisibility(View.VISIBLE);
                    changeViewTv.setText(getResources().getString(R.string.SHOW_IN_LIST_MODE));
                }
            }
        });
    }

    public void setInitVariables(){
        backImgv.setVisibility(View.GONE);
        changeViewTv.setText(getResources().getString(R.string.SHOW_IN_LIST_MODE));
    }

    private void setUpPager() {

        List<String> textList = new ArrayList<String>();

        for(int i=0; i< 200; i++){
            textList.add("Catched item no : " + i);
        }

        UserCatchedPostAdapter adapter = new UserCatchedPostAdapter(getContext(), textList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        postCatchedRecyclerView.setLayoutManager(mLayoutManager);
        postCatchedRecyclerView.setAdapter(adapter);
        //shareNormViewRecyclerView.setNestedScrollingEnabled(false);

    }


}
