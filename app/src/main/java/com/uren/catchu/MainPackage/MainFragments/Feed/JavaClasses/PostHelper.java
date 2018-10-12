package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostLikeProcess;
import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities.ImageActivity;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities.VideoActivity;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PersonListFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.ProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.PostListResponse;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class PostHelper {

    public static class LikeClicked {

        static Post post;
        static boolean isPostLiked;

        public static final void startProcess(Context context1, Post post1, boolean isPostLiked1){
            post = post1;
            isPostLiked = isPostLiked1;

            LikeClicked likeClicked = new LikeClicked(context1);
        }

        private LikeClicked(Context context) {
            postLikeClickedProcess(context);
        }

        private void postLikeClickedProcess(final Context context) {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startPostLikeClickedProcess(context, token);
                }
            });

        }

        private void startPostLikeClickedProcess(Context context, String token) {

            BaseRequest baseRequest = getBaseRequest();
            String postId = post.getPostid();
            String commentId = null;

            PostLikeProcess postLikeProcess = new PostLikeProcess(context, new OnEventListener<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse resp) {

                    if (resp == null) {
                        CommonUtils.LOG_OK_BUT_NULL("PostLikeProcess");
                    } else {
                        CommonUtils.LOG_OK("PostLikeProcess");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.LOG_FAIL("PostLikeProcess", e.toString());
                }

                @Override
                public void onTaskContinue() {
                }
            }, postId, commentId, baseRequest, isPostLiked, token);

            postLikeProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

        private BaseRequest getBaseRequest() {

            BaseRequest baseRequest = new BaseRequest();
            User user = new User();
            user.setUserid(AccountHolderInfo.getUserID());
            baseRequest.setUser(user);

            return baseRequest;
        }
    }

    public static class LikeListClicked {

        static BaseFragment.FragmentNavigation fragmentNavigation;
        static String toolbarTitle;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav, String toolbarTitle){

            fragmentNavigation = fragmNav;
            LikeListClicked.toolbarTitle = toolbarTitle;


            LikeListClicked likeListClicked = new LikeListClicked(context);
        }

        private LikeListClicked(Context context) {
            postLikeListClickedProcess(context);
        }

        private void postLikeListClickedProcess(Context context) {
            if (fragmentNavigation != null) {
                fragmentNavigation.pushFragment(PersonListFragment.newInstance(toolbarTitle), ANIMATE_DOWN_TO_UP);
            }
        }


    }

    public static class CommentListClicked {

        static BaseFragment.FragmentNavigation fragmentNavigation;
        static String toolbarTitle;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav, String toolbarTitle){

            fragmentNavigation = fragmNav;
            CommentListClicked.toolbarTitle= toolbarTitle;

            CommentListClicked commentListClicked = new CommentListClicked(context);
        }

        private CommentListClicked(Context context) {
            postCommentListClickedProcess(context);
        }

        private void postCommentListClickedProcess(Context context) {
            if (fragmentNavigation != null) {
                fragmentNavigation.pushFragment(PersonListFragment.newInstance(toolbarTitle), ANIMATE_DOWN_TO_UP);
            }
        }

    }

    public static class ViewPagerItemClicked {

        static Media media;

        public static final void startProcess(Activity activity, Context context, Media m){

            media = m;
            ViewPagerItemClicked viewPagerItemClicked = new ViewPagerItemClicked(activity, context);
        }

        private ViewPagerItemClicked(Activity activity, Context context) {

            showItemInFullView(activity,  media);

        }

        private void showItemInFullView(Activity activity, Media media) {

            PostItem.getInstance().setMedia(media);

            if (media.getType().equals(IMAGE_TYPE)) {
                Intent intent = new Intent(activity, ImageActivity.class);
                activity.startActivity(intent);
            } else if (media.getType().equals(VIDEO_TYPE)) {
                Intent intent = new Intent(activity, VideoActivity.class);
                activity.startActivity(intent);
            } else {
                Log.e("info", "unknown media type detected");
            }

        }


    }

    public static class ProfileClicked {

        static BaseFragment.FragmentNavigation fragmentNavigation;
        static String selectedProfileId;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav, String selectedProfileId){

            fragmentNavigation = fragmNav;
            ProfileClicked.selectedProfileId= selectedProfileId;

            ProfileClicked commentListClicked = new ProfileClicked(context);
        }

        private ProfileClicked(Context context) {
            postProfileClickedProcess(context);
        }

        private void postProfileClickedProcess(Context context) {
            if (fragmentNavigation != null) {
                FollowInfoListItem followInfoListItem = getProfileItem();
                if(followInfoListItem.getResultArrayItem().getUserid().equals(AccountHolderInfo.getUserID())){
                    //clicked own profile
                    fragmentNavigation.pushFragment(ProfileFragment.newInstance(false), ANIMATE_RIGHT_TO_LEFT);
                }else{
                    //clicked others profile
                    fragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
                }
            }
        }

        public FollowInfoListItem getProfileItem() {
            FollowInfoResultArrayItem followInfoResultArrayItem = new FollowInfoResultArrayItem();
            followInfoResultArrayItem.setIsFollow(true);
            followInfoResultArrayItem.setUserid(selectedProfileId);
            FollowInfoListItem profileItem = new FollowInfoListItem(followInfoResultArrayItem);
            return profileItem;
        }
    }


}
