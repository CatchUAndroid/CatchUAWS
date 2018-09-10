package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uren.catchu.GeneralUtils.CircleTransform;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.R;

import java.util.List;

import catchu.model.FollowInfoResultArrayItem;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {

    private Context context;
    private List<FollowInfoResultArrayItem> followList;

    public FollowAdapter(Context context, List<FollowInfoResultArrayItem> followList) {

        this.context = context;
        this.followList = followList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.follow_vert_list_item, parent, false);

        return new MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        ImageView profileImage;
        Button btnFollowStatus;
        CardView cardView;
        FollowInfoResultArrayItem followListItem;

        int position;

        public MyViewHolder(View view) {
            super(view);

            profileName = (TextView) view.findViewById(R.id.profile_name);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            btnFollowStatus = (Button) view.findViewById(R.id.btnFollowStatus);
            cardView = (CardView) view.findViewById(R.id.card_view);

            btnFollowStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /*takip ediyorsak takibi bırak*/
                    if(followListItem.getIsFollow()!= null && followListItem.getIsFollow()){

                        if(followListItem.getIsPrivateAccount()!=null && followListItem.getIsPrivateAccount()){
                            openDialogBox();
                        }else{
                            followListItem.setIsFollow(false);
                            followListItem.setIsPendingRequest(false);
                            btnFollowStatus.setText(R.string.follow);
                        }
                        changeColor();
                    }/*İstek gönderdiysek isteği iptal et*/
                    else if(followListItem.getIsFollow()!= null && followListItem.getIsPendingRequest()){

                        followListItem.setIsFollow(false);
                        followListItem.setIsPendingRequest(false);
                        btnFollowStatus.setText(R.string.follow);

                    }/*Takip etmiyorsak istek gönder*/
                    else{

                        if(followListItem.getIsPrivateAccount()!=null && followListItem.getIsPrivateAccount()){
                            followListItem.setIsPendingRequest(true);
                            btnFollowStatus.setText(R.string.request_sended);
                        }else{
                            followListItem.setIsFollow(true);
                            btnFollowStatus.setText(R.string.following);
                        }

                    }

                    notifyItemChanged(position, followListItem.getIsPendingRequest());
                    notifyItemChanged(position, followListItem.getIsFollow());

                }
            });


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.showToast(context, "Clicked : " + followListItem.getName());
                }
            });

        }

        public void setData(FollowInfoResultArrayItem rowItem, int position) {

            this.profileName.setText(rowItem.getName());
            this.followListItem = rowItem;
            this.position=position;

            Picasso.with(context)
                    .load(followListItem.getProfilePhotoUrl())
                    .transform(new CircleTransform())
                    .into(profileImage);

            updateUIValue();

        }



        public void updateUIValue(){

                if(followListItem.getIsFollow()!= null && followListItem.getIsFollow()){
                    btnFollowStatus.setText(R.string.following);
                }else if(followListItem.getIsPendingRequest()!= null && followListItem.getIsPendingRequest()){
                    btnFollowStatus.setText(R.string.request_sended);
                }else{
                    btnFollowStatus.setText(R.string.follow);
                }

        }


        private void openDialogBox() {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            followListItem.setIsFollow(false);
                            followListItem.setIsPendingRequest(false);
                            btnFollowStatus.setText(R.string.follow);
                            notifyItemChanged(position, followListItem.getIsPendingRequest());
                            notifyItemChanged(position, followListItem.getIsFollow());

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.cancel_following).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener).show();


        }

        private void changeColor(){


            Drawable background = btnFollowStatus.getBackground();
            if (background instanceof ShapeDrawable) {
                // cast to 'ShapeDrawable'
                ShapeDrawable shapeDrawable = (ShapeDrawable) background;
                shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, R.color.red));
            } else if (background instanceof GradientDrawable) {
                // cast to 'GradientDrawable'
                GradientDrawable gradientDrawable = (GradientDrawable) background;
                gradientDrawable.setColor(ContextCompat.getColor(context, R.color.red));
            } else if (background instanceof ColorDrawable) {
                // alpha value may need to be set again after this call
                ColorDrawable colorDrawable = (ColorDrawable) background;
                colorDrawable.setColor(ContextCompat.getColor(context, R.color.red));
            }else if(background instanceof RippleDrawable){
                //RippleDrawable colorDrawable = (RippleDrawable) background;
                //colorDrawable.setColor(ContextCompat.getColor(context, R.color.red));
            }
            //deneme
            int a=5;


        }


    }




    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        FollowInfoResultArrayItem followInfoResultArrayItem = followList.get(position);
        holder.setData(followInfoResultArrayItem, position);

    }

    @Override
    public int getItemCount() {
        return followList.size();
    }
}


