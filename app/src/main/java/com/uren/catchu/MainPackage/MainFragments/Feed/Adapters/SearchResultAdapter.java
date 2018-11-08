package com.uren.catchu.MainPackage.MainFragments.Feed.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.ApiModelsProcess.AccountHolderFollowProcess;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses.PostHelper;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.FollowInfoResultArrayItem;
import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.MyViewHolder> {

    private Context mContext;
    private List<User> personList;
    private BaseFragment.FragmentNavigation fragmentNavigation;
    GradientDrawable imageShape;

    public SearchResultAdapter(Context context, BaseFragment.FragmentNavigation fragmentNavigation) {
        this.mContext = context;
        this.fragmentNavigation = fragmentNavigation;
        this.personList = new ArrayList<User>();

        imageShape = ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                0, GradientDrawable.OVAL, 50, 0);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.follow_vert_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView shortUserNameTv;
        ImageView profileImage;
        Button btnFollowStatus;
        CardView cardView;
        User person;
        int position;

        public MyViewHolder(View view) {
            super(view);

            profileName = (TextView) view.findViewById(R.id.profile_name);
            shortUserNameTv = view.findViewById(R.id.shortUserNameTv);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            btnFollowStatus = (Button) view.findViewById(R.id.btnFollowStatus);
            cardView = (CardView) view.findViewById(R.id.card_view);
            profileImage.setBackground(imageShape);

            setListeners();

        }

        private void setListeners() {

            btnFollowStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnFollowStatus.setEnabled(false);
                    btnFollowStatus.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));
                    manageFollowStatus();
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FollowInfoResultArrayItem rowItem = new FollowInfoResultArrayItem();
                    rowItem.setUserid(person.getUserid());
                    rowItem.setProfilePhotoUrl(person.getProfilePhotoUrl());
                    rowItem.setName(person.getName());
                    FollowInfoListItem followInfoListItem = new FollowInfoListItem(rowItem);
                    PostHelper.ProfileClicked.startProcess(mContext, fragmentNavigation, followInfoListItem);
                }
            });

        }

        public void manageFollowStatus() {

            //takip ediliyor ise
            if (person.getFollowStatus().equals(FOLLOW_STATUS_FOLLOWING)) {
                if (person.getIsPrivateAccount() != null && person.getIsPrivateAccount()) {
                    openDialogBox();
                } else {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }
            } else if (person.getFollowStatus().equals(FOLLOW_STATUS_PENDING)) {
                //istek gonderilmis ise
                updateFollowStatus(FRIEND_DELETE_PENDING_FOLLOW_REQUEST);
            } else if (person.getFollowStatus().equals(FOLLOW_STATUS_NONE)) {
                //takip istegi yok ise
                if (person.getIsPrivateAccount() != null && person.getIsPrivateAccount()) {
                    updateFollowStatus(FRIEND_FOLLOW_REQUEST);
                } else {
                    updateFollowStatus(FRIEND_CREATE_FOLLOW_DIRECTLY);
                }
            } else {
                //do nothing
            }
        }

        public void setData(User selectedFriend, int position) {

            /*
            this.position = position;
            this.selectedFriend = selectedFriend;
            this.requestedUserid = selectedFriend.getUserid();
            setUserName();
            UserDataUtil.setName(selectedFriend.getName(), nameTextView);
            UserDataUtil.setProfilePicture(context, selectedFriend.getProfilePhotoUrl(),
                    selectedFriend.getName(), shortenTextView, profilePicImgView);

            if(selectedFriend.getUserid().equals(AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()))
                statuDisplayBtn.setVisibility(View.GONE);
            else {
                UserDataUtil.updateFollowButton(context, selectedFriend.getFriendRelation(), selectedFriend.getPendingFriendRequest(), statuDisplayBtn, false);
                statuDisplayBtn.setVisibility(View.VISIBLE);
            }


            */
        }


        private void openDialogBox() {

            YesNoDialogBoxCallback yesNoDialogBoxCallback = new YesNoDialogBoxCallback() {
                @Override
                public void yesClick() {
                    updateFollowStatus(FRIEND_DELETE_FOLLOW);
                }

                @Override
                public void noClick() {

                }
            };

            DialogBoxUtil.showYesNoDialog(mContext, "", mContext.getString(R.string.cancel_following), yesNoDialogBoxCallback);
        }

        private void updateFollowStatus(final String requestType) {

            AccountHolderFollowProcess.friendFollowRequest(requestType, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid()
                    , person.getUserid(), new CompleteCallback() {
                        @Override
                        public void onComplete(Object object) {
                            updateFollowUI(requestType);
                            btnFollowStatus.setEnabled(true);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            DialogBoxUtil.showErrorDialog(mContext, mContext.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                                @Override
                                public void okClick() {
                                }
                            });
                            btnFollowStatus.setEnabled(true);
                        }
                    });
        }

        private void updateFollowUI(String updateType) {
            AccountHolderInfo.updateAccountHolderFollowCnt(updateType);
            updateFollowTypeAfterOperation(updateType);
            notifyItemChanged(position, person.getFollowStatus());
        }

        public void updateFollowTypeAfterOperation(String updateType) {
            switch (updateType) {
                case FRIEND_DELETE_FOLLOW:
                    person.setFollowStatus(FOLLOW_STATUS_NONE);
                    break;

                case FRIEND_DELETE_PENDING_FOLLOW_REQUEST:
                    person.setFollowStatus(FOLLOW_STATUS_NONE);
                    break;

                case FRIEND_FOLLOW_REQUEST:
                    person.setFollowStatus(FOLLOW_STATUS_PENDING);
                    break;

                case FRIEND_CREATE_FOLLOW_DIRECTLY:
                    person.setFollowStatus(FOLLOW_STATUS_FOLLOWING);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        User user = personList.get(position);
        holder.setData(user, position);
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public void updateAdapterWithPosition(int position) {

        notifyItemChanged(position);
    }

    public List<User> getPersonList() {
        return personList;
    }

    public void addAll(UserListResponse userListResponse) {

        if (userListResponse != null) {

            personList.addAll(userListResponse.getItems());
            notifyItemRangeInserted(personList.size(), personList.size() + userListResponse.getItems().size());

        }
    }

}


