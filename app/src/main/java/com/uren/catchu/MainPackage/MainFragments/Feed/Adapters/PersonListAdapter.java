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
import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.YesNoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.CompleteCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PersonListItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.User;
import catchu.model.UserListResponse;

import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_FOLLOWING;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_NONE;
import static com.uren.catchu.Constants.StringConstants.FOLLOW_STATUS_PENDING;
import static com.uren.catchu.Constants.StringConstants.FRIEND_CREATE_FOLLOW_DIRECTLY;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_FOLLOW;
import static com.uren.catchu.Constants.StringConstants.FRIEND_DELETE_PENDING_FOLLOW_REQUEST;
import static com.uren.catchu.Constants.StringConstants.FRIEND_FOLLOW_REQUEST;

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.MyViewHolder> {

    private Context context;
    private UserListResponse personList;
    private PersonListItemClickListener personListItemClickListener;
    GradientDrawable imageShape;
    GradientDrawable buttonShape;

    public PersonListAdapter(Context context, UserListResponse personList, PersonListItemClickListener personListItemClickListener) {
        this.context = context;
        this.personList = personList;
        this.personListItemClickListener = personListItemClickListener;
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

            btnFollowStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnFollowStatus.setEnabled(false);
                    btnFollowStatus.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                    manageFollowStatus();
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    personListItemClickListener.onPersonListItemClicked(v, person, position);
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

        public void setData(User person, int position) {

            this.profileName.setText(person.getName());
            this.person = person;
            this.position = position;
            UserDataUtil.setProfilePicture(context, person.getProfilePhotoUrl(),
                    person.getName(), person.getUsername(), shortUserNameTv, profileImage);

            UserDataUtil.updateFollowButton2(context, person.getFollowStatus(), btnFollowStatus, true);
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

            DialogBoxUtil.showYesNoDialog(context, "", context.getString(R.string.cancel_following), yesNoDialogBoxCallback);
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
                            DialogBoxUtil.showErrorDialog(context, context.getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
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
        User user = personList.getItems().get(position);
        holder.setData(user, position);
    }

    @Override
    public int getItemCount() {
        return personList.getItems().size();
    }

    public void updateAdapterWithPosition(int position) {

        notifyItemChanged(position);
    }

    public UserListResponse getPersonList() {
        return personList;
    }

}


