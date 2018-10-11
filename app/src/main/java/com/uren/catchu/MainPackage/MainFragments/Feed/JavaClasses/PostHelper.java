package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import android.content.Context;
import android.os.AsyncTask;

import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostLikeProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.Singleton.AccountHolderInfo;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;
import catchu.model.Post;
import catchu.model.User;

public class PostHelper {

    public static class LikeClicked {

        Context context;
        Post post;
        boolean isPostLiked;

        public void setInstance(Context context, Post post, boolean isPostLiked){
            this.context = context;
            this.post = post;
            this.isPostLiked = isPostLiked;

            LikeClicked likeClicked = new LikeClicked();
        }

        public LikeClicked() {
            postLikeClickedProcess();
        }

        public void postLikeClickedProcess() {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startPostLikeClickedProcess(context, post, isPostLiked, token);
                }
            });

        }

        private void startPostLikeClickedProcess(Context context, Post post, boolean isPostLiked, String token) {

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


}
