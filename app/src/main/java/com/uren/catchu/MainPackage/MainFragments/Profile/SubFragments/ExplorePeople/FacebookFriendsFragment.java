package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.uren.catchu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import catchu.model.GroupRequestResult;
import catchu.model.GroupRequestResultResultArrayItem;

public class FacebookFriendsFragment extends Fragment{

    View mView;

    public FacebookFriendsFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_special_select, container, false);
            ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initVariables();
        addListeners();
        getFacebookFriends();
    }

    public void initVariables(){

    }

    private void addListeners() {

    }

    public void getFacebookFriends() {

    }




   /* @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.trim().isEmpty())
                    groupRequestResult = orgGroupRequestResult;
                else {
                    GroupRequestResult tempGroupRequestResult = new GroupRequestResult();
                    List<GroupRequestResultResultArrayItem> listItem = new ArrayList<>();
                    tempGroupRequestResult.setResultArray(listItem);

                    for (GroupRequestResultResultArrayItem item : orgGroupRequestResult.getResultArray()) {
                        if (item.getName().toLowerCase().contains(searchString.toLowerCase()))
                            tempGroupRequestResult.getResultArray().add(item);
                    }

                    groupRequestResult = tempGroupRequestResult;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = groupRequestResult;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupRequestResult = (GroupRequestResult) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }*/
}
