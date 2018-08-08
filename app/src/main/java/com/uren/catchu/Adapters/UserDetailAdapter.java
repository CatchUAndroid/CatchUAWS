package com.uren.catchu.Adapters;

import android.app.Activity;
import android.content.Context;
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
import com.uren.catchu.ApiGatewayFunctions.GroupResultProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.GeneralUtils.ImageCache.ImageLoader;
import com.uren.catchu.GeneralUtils.PermissionModule;
import com.uren.catchu.R;

import catchu.model.FriendRequest;
import catchu.model.FriendRequestList;
import catchu.model.GroupRequestResult;
import catchu.model.SearchResult;
import catchu.model.SearchResultResultArrayItem;

import static com.facebook.FacebookSdk.getApplicationContext;
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

        String requesterUserid;
        String requestedUserid;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = (ImageView) view.findViewById(R.id.profilePicImgView);
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

            // TODO: 8.08.2018 --> Butona basildiginda o andaki item refresh olmasi icin tekrar servis call edilecek.. 
            if(friendRelation){
                processFriendRequest(FRIEND_DELETE_FOLLOW);
                statuDisplayBtn.setText(context.getResources().getString(R.string.upperAddFriend));
                statuDisplayBtn.setBackgroundColor(context.getResources().getColor(R.color.background, null));
            }else {
                if(pendingFriendRequest){
                    processFriendRequest(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
                    statuDisplayBtn.setText(context.getResources().getString(R.string.upperAddFriend));
                    statuDisplayBtn.setBackgroundColor(context.getResources().getColor(R.color.background, null));
                }
                else{
                    if(isPrivateAccount){
                        processFriendRequest(FRIEND_FOLLOW_REQUEST);
                        statuDisplayBtn.setText(context.getResources().getString(R.string.upperRequested));
                        statuDisplayBtn.setBackgroundColor(context.getResources().getColor(R.color.black_25_transparent, null));
                    }else{
                        processFriendRequest(FRIEND_CREATE_FOLLOW_DIRECTLY);
                        statuDisplayBtn.setText(context.getResources().getString(R.string.upperFriend));
                        statuDisplayBtn.setBackgroundColor(context.getResources().getColor(R.color.green, null));
                    }
                }
            }
        }

        public void processFriendRequest(String requestType){

            Log.i("Info", "processFriendRequest starts++++++++++++++++++++++++++");
            Log.i("Info", "   >>requestType    :" + requestType);
            Log.i("Info", "   >>userid         :" + userid);
            Log.i("Info", "   >>requestedUserid:" + requestedUserid);

            final FriendRequestProcess friendRequestProcess = new FriendRequestProcess(new OnEventListener<FriendRequestList>() {

                @Override
                public void onSuccess(FriendRequestList object) {
                    Log.i("Info", "Request operation is successful");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.i("Info", "Request operation is failed !!!!");
                    Toast.makeText(context, context.getResources().getString(R.string.error) + e.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTaskContinue() {

                }
            }, requestType, userid, requestedUserid);

            friendRequestProcess.execute();
        }

        public void setData(SearchResultResultArrayItem selectedFriend, int position) {
            this.nameTextView.setText(selectedFriend.getName());
            this.position = position;
            this.selectedFriend = selectedFriend;
            this.isPrivateAccount = selectedFriend.getIsPrivateAccount();
            this.friendRelation = selectedFriend.getFriendRelation();
            this.pendingFriendRequest = selectedFriend.getPendingFriendRequest();
            this.requestedUserid = selectedFriend.getUserid();
            imageLoader.DisplayImage(selectedFriend.getProfilePhotoUrl(), profilePicImgView, displayRounded);

            if(friendRelation){
                statuDisplayBtn.setText(context.getResources().getString(R.string.upperFriend));
                statuDisplayBtn.setBackgroundColor(context.getResources().getColor(R.color.green, null));
            }else {
                if(pendingFriendRequest) {
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
