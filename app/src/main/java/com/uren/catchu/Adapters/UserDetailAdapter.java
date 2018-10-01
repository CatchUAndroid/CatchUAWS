package com.uren.catchu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uren.catchu.ApiGatewayFunctions.FriendRequestProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.FriendRequestList;
import catchu.model.RelationProperties;
import catchu.model.SearchResult;
import catchu.model.SearchResultResultArrayItem;

import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.displayRounded;
import static com.uren.catchu.Constants.StringConstants.friendsCacheDirectory;

public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.MyViewHolder>{

    View view;
    LayoutInflater layoutInflater;
    String userid;
    public ImageLoader imageLoader;

    String searchText;
    Context context;
    Activity activity;
    SearchResult searchResult;

    public UserDetailAdapter(Context context, String searchText, SearchResult searchResult, String userid) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.searchText = searchText;
        this.searchResult = searchResult;
        this.userid = userid;
        activity = (Activity) context;
        imageLoader=new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);

        Log.i("Info", "UserDetailAdapter +++++++++++++++++++++++++");
        writeSearchResult();

    }

    public void writeSearchResult(){
        Log.i("Info", "writeSearchResult +++++++++++++++++++++++++");

        for(int i=0; i < searchResult.getResultArray().size(); i++){

            Log.i("Info", "   >>loginUser name      :" + searchResult.getResultArray().get(i).getName());
            Log.i("Info", "   >>friend relation:" + searchResult.getResultArray().get(i).getFriendRelation());
            Log.i("Info", "   >>pend. request  :" + searchResult.getResultArray().get(i).getPendingFriendRequest());
            Log.i("Info", "   >>=====================================");
        }
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
        TextView usernameTextView;
        ImageView profilePicImgView;
        SearchResultResultArrayItem selectedFriend;
        Button statuDisplayBtn;
        String requestedUserid;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = (ImageView) view.findViewById(R.id.profilePicImgView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            statuDisplayBtn = (Button) view.findViewById(R.id.statuDisplayBtn);

            statuDisplayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    checkFriendRelation();
                }
            });
        }

        public void checkFriendRelation(){

            if(selectedFriend.getFriendRelation())
                processFriendRequest(FRIEND_DELETE_FOLLOW);
            else {
                if(selectedFriend.getPendingFriendRequest())
                    processFriendRequest(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
                else{
                    if(selectedFriend.getIsPrivateAccount()){
                        processFriendRequest(FRIEND_FOLLOW_REQUEST);
                    }else{
                        processFriendRequest(FRIEND_CREATE_FOLLOW_DIRECTLY);
                    }
                }
            }
        }

        public void processFriendRequest(final String requestType){

            Log.i("Info", "processFriendRequest starts++++++++++++++++++++++++++");
            Log.i("Info", "   >>requestType    :" + requestType);
            Log.i("Info", "   >>userid         :" + userid);
            Log.i("Info", "   >>requestedUserid:" + requestedUserid);


            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startFriendRequest(requestType, token);
                }
            });



        }

        private void startFriendRequest(String requestType, String token) {

            final FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {

                @Override
                public void onSuccess(FriendRequestList object) {
                    RelationProperties relationProperties = object.getUpdatedUserRelationInfo();

                    Log.i("Info", "   ==========================");
                    Log.i("Info", "   >>getFriendRelation      :" + relationProperties.getFriendRelation());
                    Log.i("Info", "   >>getPendingFriendRequest:" + relationProperties.getPendingFriendRequest());

                    selectedFriend.setFriendRelation(relationProperties.getFriendRelation());
                    selectedFriend.setPendingFriendRequest(relationProperties.getPendingFriendRequest());

                    searchResult.getResultArray().remove(position);
                    searchResult.getResultArray().add(position, selectedFriend);

                    updateUIValue();

                    writeSearchResult();
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.showToastLong(context, context.getResources().getString(R.string.error) + e.toString());
                }

                @Override
                public void onTaskContinue() {

                }
            }, requestType, userid, requestedUserid, token);

            friendRequestProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

        public void setData(SearchResultResultArrayItem selectedFriend, int position) {
            this.nameTextView.setText(selectedFriend.getName());
            this.usernameTextView.setText(selectedFriend.getUsername());
            this.position = position;
            this.selectedFriend = selectedFriend;
            this.requestedUserid = selectedFriend.getUserid();
            imageLoader.DisplayImage(selectedFriend.getProfilePhotoUrl(), profilePicImgView, displayRounded);

            updateUIValue();
        }

        public void updateUIValue(){

            if(selectedFriend.getFriendRelation()){
                statuDisplayBtn.setText(context.getResources().getString(R.string.upperFriend));
                statuDisplayBtn.setBackgroundColor(context.getResources().getColor(R.color.green, null));
            }else {
                if(selectedFriend.getPendingFriendRequest()) {
                    statuDisplayBtn.setText(context.getResources().getString(R.string.upperRequested));
                    statuDisplayBtn.setBackgroundColor(context.getResources().getColor(R.color.black_25_transparent, null));
                }
                else {
                    statuDisplayBtn.setText(context.getResources().getString(R.string.upperAddFriend));
                    statuDisplayBtn.setBackgroundColor(context.getResources().getColor(R.color.background, null));
                }
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
