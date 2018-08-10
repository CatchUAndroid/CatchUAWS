package com.uren.catchu.GroupPackage.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uren.catchu.GroupPackage.Adapters.FriendGridListAdapter;
import com.uren.catchu.GroupPackage.Adapters.SelectFriendAdapter;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.SelectedFriendList;

import butterknife.ButterKnife;

import static com.uren.catchu.Constants.StringConstants.gridShown;
import static com.uren.catchu.Constants.StringConstants.horizontalShown;
import static com.uren.catchu.Constants.StringConstants.verticalShown;

@SuppressLint("ValidFragment")
public class SelectedFriendFragment extends Fragment {

    RecyclerView personRecyclerView;

    private View mView;
    String viewType;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    private static SelectedFriendList selectedFriendListInstance;

    @SuppressLint("ValidFragment")
    public SelectedFriendFragment(String viewType) {
        this.viewType = viewType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        personRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        getData();
    }

    public void getData(){

        selectedFriendListInstance = SelectedFriendList.getInstance();

        switch (viewType){

            case verticalShown:

                SelectFriendAdapter selectFriendAdapter = null;
                selectFriendAdapter = new SelectFriendAdapter(getActivity(), selectedFriendListInstance.getSelectedFriendList());

                personRecyclerView.setAdapter(selectFriendAdapter);
                linearLayoutManager  = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case horizontalShown:
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case gridShown:
                FriendGridListAdapter friendGridListAdapter = new FriendGridListAdapter(getActivity(),
                        selectedFriendListInstance.getSelectedFriendList());
                personRecyclerView.setAdapter(friendGridListAdapter);
                gridLayoutManager =new GridLayoutManager(getActivity(), 4);
                personRecyclerView.setLayoutManager(gridLayoutManager);
                break;

            default:
                Toast.makeText(getActivity(), "SelectedPersonFragment Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }
}
