package com.uren.catchu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GeneralUtils.PermissionModule;
import com.uren.catchu.R;

import catchu.model.SearchResult;
import catchu.model.SearchResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.MyViewHolder>{

    View view;
    LayoutInflater layoutInflater;
    public ImageLoader imageLoader;

    String searchText;
    Context context;
    Activity activity;
    SearchResult searchResult;

    public UserDetailAdapter(Context context, String searchText, SearchResult searchResult) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.searchText = searchText;
        this.searchResult = searchResult;
        activity = (Activity) context;
        imageLoader=new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
    }

    @NonNull
    @Override
    public UserDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.person_vert_list_item, viewGroup, false);
        final UserDetailAdapter.MyViewHolder holder = new UserDetailAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailAdapter.MyViewHolder myViewHolder, int position) {

        SearchResultResultArrayItem selectedFriend = searchResult.getResultArray().get(position);
        myViewHolder.setData(selectedFriend, position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView profilePicImgView;
        SearchResultResultArrayItem selectedFriend;
        Button statuDisplayBtn;
        boolean isPrivateAccount;
        boolean friendRelation;
        boolean pendingFriendRequest;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = (ImageView) view.findViewById(R.id.profilePicImgView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            statuDisplayBtn = (Button) view.findViewById(R.id.statuDisplayBtn);

            statuDisplayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void setData(SearchResultResultArrayItem selectedFriend, int position) {
            this.nameTextView.setText(selectedFriend.getName());
            this.position = position;
            this.selectedFriend = selectedFriend;
            this.isPrivateAccount = selectedFriend.getIsPrivateAccount();
            this.friendRelation = selectedFriend.getFriendRelation();
            this.pendingFriendRequest = selectedFriend.getPendingFriendRequest();
            imageLoader.DisplayImage(selectedFriend.getProfilePhotoUrl(), profilePicImgView, displayRounded);

            if(friendRelation){
                statuDisplayBtn.setText(context.getResources().getString(R.string.upperFriend));
            }else {
                if(pendingFriendRequest)
                    statuDisplayBtn.setText(context.getResources().getString(R.string.upperRequested));
                else
                    statuDisplayBtn.setText(context.getResources().getString(R.string.upperAddFriend));
            }
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return searchResult.getResultArray().size();
    }
}
