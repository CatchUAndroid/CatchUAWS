package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uren.catchu.Adapters.LocationTrackerAdapter;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostCommentProcess;
import com.uren.catchu.ApiGatewayFunctions.PostDeleteProcess;
import com.uren.catchu.ApiGatewayFunctions.PostLikeProcess;
import com.uren.catchu.ApiGatewayFunctions.PostPatchProcess;
import com.uren.catchu.ApiGatewayFunctions.ReportProblemProcess;
import com.uren.catchu.GeneralUtils.BitmapConversion;
import com.uren.catchu.GeneralUtils.ClickableImage.ClickableImageView;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;

import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.FeedRefreshCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.Interfaces.PostFeaturesCallback;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.CommentListFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PersonListFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PostImageViewFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PostVideoPlayFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.SinglePostFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.UserInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.ProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.Permissions.PermissionModule;
import com.uren.catchu.R;
import com.uren.catchu.MainPackage.MainFragments.Share.Interfaces.LocationCallback;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;
import catchu.model.Comment;
import catchu.model.CommentRequest;
import catchu.model.CommentResponse;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.PostRequest;
import catchu.model.Report;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.AWS_EMPTY;
import static com.uren.catchu.Constants.StringConstants.COMING_FOR_LIKE_LIST;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class PostHelper {

    public static class LikeClicked {

        static String postId;
        static String commentId;
        static boolean isLiked;

        public static final void startProcess(Context context, String postId, String commentId, boolean isPostLiked) {
            LikeClicked.postId = postId;
            LikeClicked.commentId = commentId;
            LikeClicked.isLiked = isPostLiked;

            LikeClicked likeClicked = new LikeClicked(context);
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

            String userId = AccountHolderInfo.getUserID();
            String postId = LikeClicked.postId;
            String commentId;
            if (LikeClicked.commentId == null) {
                commentId = AWS_EMPTY;
            } else {
                commentId = LikeClicked.commentId;
            }

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
            }, userId, postId, commentId, isLiked, token);

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
        static String postId;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav, String toolbarTitle,
                                              String postId) {

            fragmentNavigation = fragmNav;
            LikeListClicked.toolbarTitle = toolbarTitle;
            LikeListClicked.postId = postId;

            LikeListClicked likeListClicked = new LikeListClicked(context);
        }

        private LikeListClicked(Context context) {
            postLikeListClickedProcess(context);
        }

        private void postLikeListClickedProcess(Context context) {
            if (fragmentNavigation != null) {
                String comingFor = COMING_FOR_LIKE_LIST;
                fragmentNavigation.pushFragment(PersonListFragment.newInstance(toolbarTitle, postId, comingFor), ANIMATE_RIGHT_TO_LEFT);
            }
        }


    }

    public static class CommentListClicked {

        static BaseFragment.FragmentNavigation fragmentNavigation;
        static String postId;
        static int position;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav,
                                              String postId, int position) {

            fragmentNavigation = fragmNav;
            CommentListClicked.postId = postId;
            CommentListClicked.position = position;

            CommentListClicked commentListClicked = new CommentListClicked(context);
        }

        private CommentListClicked(Context context) {
            postCommentListClickedProcess(context);
        }

        private void postCommentListClickedProcess(Context context) {
            if (fragmentNavigation != null) {
                fragmentNavigation.pushFragment(CommentListFragment.newInstance(postId, position), ANIMATE_RIGHT_TO_LEFT);
            }
        }

    }

    public static class ViewPagerItemClicked {

        static Media media;
        static BaseFragment.FragmentNavigation mfragmentNavigation;

        public static final void startProcess(Activity activity, Context context, Media m, BaseFragment.FragmentNavigation fragmentNavigation) {
            mfragmentNavigation = fragmentNavigation;
            media = m;
            ViewPagerItemClicked viewPagerItemClicked = new ViewPagerItemClicked(activity, context);
        }

        private ViewPagerItemClicked(Activity activity, Context context) {

            showItemInFullView(activity, media);

        }

        private void showItemInFullView(Activity activity, Media media) {

            SingletonPostItem.getInstance().setMedia(media);

            if (media.getType().equals(IMAGE_TYPE)) {

                if(mfragmentNavigation != null){
                    mfragmentNavigation.pushFragment(new PostImageViewFragment());
                }

            } else if (media.getType().equals(VIDEO_TYPE)) {

                if(mfragmentNavigation != null){
                    mfragmentNavigation.pushFragment(new PostVideoPlayFragment());
                }
            } else {
                Log.e("info", "unknown media type detected");
            }

        }


    }

    public static class ProfileClicked {

        static BaseFragment.FragmentNavigation fragmentNavigation;
        static UserInfoListItem userInfoListItem;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav, UserInfoListItem userInfoListItem) {

            fragmentNavigation = fragmNav;
            ProfileClicked.userInfoListItem = userInfoListItem;

            ProfileClicked commentListClicked = new ProfileClicked(context);
        }

        private ProfileClicked(Context context) {
            postProfileClickedProcess(context);
        }

        private void postProfileClickedProcess(Context context) {
            if (fragmentNavigation != null) {
                if (userInfoListItem.getUser().getUserid().equals(AccountHolderInfo.getUserID())) {
                    //clicked own profile
                    fragmentNavigation.pushFragment(ProfileFragment.newInstance(false), ANIMATE_RIGHT_TO_LEFT);
                } else {
                    //clicked others profile
                    fragmentNavigation.pushFragment(new OtherProfileFragment(userInfoListItem), ANIMATE_RIGHT_TO_LEFT);
                }
            }
        }

    }

    public static class LocatonDetailClicked {

        static BaseFragment.FragmentNavigation fragmentNavigation;
        static Post post;
        static ImageView imgProfilePic;
        static TextView txtProfilePic;
        LocationTrackerAdapter locationTrackObj;
        GoogleMap mMap;
        MapRipple mapRipple;
        static Activity mAct;
        PermissionModule permissionModule;

        public static final void startProcess(Activity activity, Context context, BaseFragment.FragmentNavigation fragmNav, Post post, TextView txtProfilePic, ImageView imgProfilePic) {

            fragmentNavigation = fragmNav;
            LocatonDetailClicked.post = post;
            LocatonDetailClicked.imgProfilePic = imgProfilePic;
            LocatonDetailClicked.mAct = activity;
            LocatonDetailClicked.txtProfilePic = txtProfilePic;

            LocatonDetailClicked locatonDetailClicked = new LocatonDetailClicked(context);
        }

        private LocatonDetailClicked(Context context) {
            postLocationDetailClickedProcess(context);
        }

        private void postLocationDetailClickedProcess(final Context context) {
            if (fragmentNavigation != null) {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.half_map_view);
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
                ImageView btnCloseMap = (ImageView) dialog.findViewById(R.id.btnCloseMap);
                btnCloseMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                MapsInitializer.initialize(context);
                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();// needed to get the map to display immediately
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        mMap.getUiSettings().setScrollGesturesEnabled(true);
                        mMap.getUiSettings().setAllGesturesEnabled(true);
                        mMap.getUiSettings().setRotateGesturesEnabled(false);

                        LatLng latLng = new LatLng(post.getLocation().getLatitude().doubleValue(),
                                post.getLocation().getLongitude().doubleValue());

                        MarkerOptions options = new MarkerOptions().position(latLng);
                        Bitmap bitmap = BitmapConversion.createUserMapBitmap(context, imgProfilePic);
                        if (bitmap != null) {
                            options.title(post.getUser().getName());
                            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                            options.anchor(0.5f, 0.907f);
                            mMap.addMarker(options);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                            if (post.getDistance().intValue() > 10) {
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);
                            } else if (post.getDistance().intValue() > 5) {
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
                            } else if (post.getDistance().intValue() > 2) {
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                            } else {
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                            }
                        } else {

                        }

                        setMyLocation(context);
                    }
                });


            }
        }

        private void setMyLocation(Context context) {
            permissionModule = new PermissionModule(context);
            initLocationTracker(context);
            checkCanGetLocation(context);
        }

        private void initLocationTracker(Context context) {
            locationTrackObj = new LocationTrackerAdapter(context, new LocationCallback() {
                @Override
                public void onLocationChanged(Location location) {
                    //showMyLocation();
                }
            });
        }

        private void checkCanGetLocation(Context context) {
            if (!locationTrackObj.canGetLocation())
                DialogBoxUtil.showSettingsAlert(mAct);
            else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permissionModule.checkAccessFineLocationPermission()) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }

        //todo NT - harita modu açıkken mapRipple'ın hareket edip etmediği test edilmeli..
        private void showMyLocation() {

            Location location = locationTrackObj.getLocation();
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mapRipple = new MapRipple(mMap, latLng, mAct);
            }
        }
    }

    public static class AddComment {

        static String postId;
        static Comment comment;
        static int position;

        public static final void startProcess(Context context, String postId, Comment comment, int position) {

            AddComment.postId = postId;
            AddComment.comment = comment;
            AddComment.position = position;

            AddComment addComment = new AddComment(context);
        }

        private AddComment(Context context) {
            postAddCommentProcess(context);
        }

        private void postAddCommentProcess(final Context context) {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startPostAddCommentProcess(context, token);
                }
            });
        }

        private void startPostAddCommentProcess(Context context, String token) {

            String userId = AccountHolderInfo.getUserID();
            final String postId = this.postId;
            String commentId = AWS_EMPTY;
            CommentRequest commentRequest = getCommentRequest();

            PostCommentProcess postCommentProcess = new PostCommentProcess(context, new OnEventListener<CommentResponse>() {
                @Override
                public void onSuccess(CommentResponse commentResponse) {
                    if (commentResponse == null) {
                        CommonUtils.LOG_OK_BUT_NULL("PostCommentProcess");
                    } else {
                        CommonUtils.LOG_OK("PostCommentProcess");
                    }

                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.LOG_FAIL("PostCommentProcess", e.toString());
                }

                @Override
                public void onTaskContinue() {

                }
            }, userId, postId, commentId, commentRequest, token);

            postCommentProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

        private CommentRequest getCommentRequest() {
            CommentRequest commentRequest = new CommentRequest();
            commentRequest.setComment(comment);
            return commentRequest;
        }

    }

    public static class PostCommentPermission {

        static Post post;
        static String userId;

        public static final void startProcess(Context context, String userId, Post post) {

            PostCommentPermission.userId = userId;
            PostCommentPermission.post = post;

            PostCommentPermission addComment = new PostCommentPermission(context);
        }

        private PostCommentPermission(Context context) {
            postCommentPermissionProcess(context);
        }

        private void postCommentPermissionProcess(final Context context) {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startPostCommentPermissionProcess(context, token);
                }
            });
        }

        private void startPostCommentPermissionProcess(Context context, String token) {

            String userId = PostCommentPermission.userId;
            String postId = PostCommentPermission.post.getPostid();
            PostRequest postRequest = getPostRequest();

            PostPatchProcess postPatchProcess = new PostPatchProcess(context, new OnEventListener<BaseResponse>() {

                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    if (baseResponse == null) {
                        CommonUtils.LOG_OK_BUT_NULL("PostPatchProcess");
                    } else {
                        CommonUtils.LOG_OK("PostPatchProcess");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.LOG_FAIL("PostPatchProcess", e.toString());
                }

                @Override
                public void onTaskContinue() {

                }
            }, userId, postId,postRequest, token);

            postPatchProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

        private PostRequest getPostRequest() {
            PostRequest postRequest = new PostRequest();
            postRequest.setPost(post);
            return postRequest;
        }

    }

    public static class DeletePost {

        static String userId;
        static String postId;

        public static final void startProcess(Context context, String userId, String postId) {

            DeletePost.userId = userId;
            DeletePost.postId = postId;

            DeletePost deletePost = new DeletePost(context);
        }

        private DeletePost(Context context) {
            deletePostProcess(context);
        }

        private void deletePostProcess(final Context context) {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startDeletePostProcess(context, token);
                }
            });
        }

        private void startDeletePostProcess(Context context, String token) {

            String userId = DeletePost.userId;
            String postId = DeletePost.postId;

            PostDeleteProcess postDeleteProcess = new PostDeleteProcess(context, new OnEventListener<BaseResponse>() {

                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    if (baseResponse == null) {
                        CommonUtils.LOG_OK_BUT_NULL("PostDeleteProcess");
                    } else {
                        CommonUtils.LOG_OK("PostDeleteProcess");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.LOG_FAIL("PostDeleteProcess", e.toString());
                }

                @Override
                public void onTaskContinue() {

                }
            }, userId, postId, token);

            postDeleteProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }

    public static class ReportPost {

        static String userId;
        static String postId;
        static Report report;

        public static final void startProcess(Context context, String userId, String postId, Report report) {

            ReportPost.userId = userId;
            ReportPost.postId = postId;
            ReportPost.report = report;

            ReportPost reportPost = new ReportPost(context);
        }

        private ReportPost(Context context) {
            reportPostProcess(context);
        }

        private void reportPostProcess(final Context context) {

            AccountHolderInfo.getToken(new TokenCallback() {
                @Override
                public void onTokenTaken(String token) {
                    startDeletePostProcess(context, token);
                }
            });
        }

        private void startDeletePostProcess(Context context, String token) {

            String userId = ReportPost.userId;
            String postId = ReportPost.postId;
            Report report = new Report();

            ReportProblemProcess reportProblemProcess = new ReportProblemProcess(new OnEventListener() {
                @Override
                public void onSuccess(Object object) {
                    if (object == null) {
                        CommonUtils.LOG_OK_BUT_NULL("ReportProblemProcess");
                    } else {
                        CommonUtils.LOG_OK("ReportProblemProcess");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    CommonUtils.LOG_FAIL("ReportProblemProcess", e.toString());
                }

                @Override
                public void onTaskContinue() {

                }
            }, userId, token,report, postId);

            reportProblemProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }


    public static class SinglePostClicked {

        /**
         * Bu fonksiyon çağrılırken sırasıyla
         * getInstance()
         * setSinglePostItems()
         * setPostFeaturesCallback
         * startSinglePostProcess() fonksiyonları zorunlu olarak çağrılmalıdır.
         */

        private static SinglePostClicked instance = null;
        private static List<PostFeaturesCallback> postFeaturesCallbackList;

        private static BaseFragment.FragmentNavigation fragmentNavigation;
        private static String toolbarTitle;
        private static String postId;
        private static int position;
        private static int numberOfCallback;

        public SinglePostClicked() {
            postFeaturesCallbackList = new ArrayList<PostFeaturesCallback>();
            numberOfCallback = -1;
        }

        public static SinglePostClicked getInstance() {
            if (instance == null)
                instance = new SinglePostClicked();

            return instance;
        }


        public void setSinglePostItems(Context context, BaseFragment.FragmentNavigation fragmentNavigation,
                                       String toolbarTitle,
                                       String postId,
                                       int position) {

            SinglePostClicked.fragmentNavigation = fragmentNavigation;
            SinglePostClicked.toolbarTitle = toolbarTitle;
            SinglePostClicked.postId = postId;
            SinglePostClicked.position = position;

        }

        public void startSinglePostProcess() {
            if (fragmentNavigation != null) {
                numberOfCallback++;
                fragmentNavigation.pushFragment(SinglePostFragment.newInstance(toolbarTitle, postId, position, numberOfCallback), ANIMATE_RIGHT_TO_LEFT);
            }
        }

        public void setPostFeaturesCallback(PostFeaturesCallback postFeaturesCallback) {
            postFeaturesCallbackList.add(postFeaturesCallback);
        }
        public static void postLikeStatusChanged(boolean isPostLiked, int newLikeCount, int position, int _numberOfCallback) {
            postFeaturesCallbackList.get(_numberOfCallback).onPostLikeClicked(isPostLiked, newLikeCount, position);
        }
        public static void postCommentCountChanged(int position, int newCommentCount, int _numberOfCallback) {
            postFeaturesCallbackList.get(_numberOfCallback).onCommentAdd(position, newCommentCount);
        }
        public static void postCommentAllowedStatusChanged(int position, boolean newCommentAllowed, int _numberOfCallback) {
            postFeaturesCallbackList.get(_numberOfCallback).onCommentAllowedStatusChanged(position, newCommentAllowed);
        }
        public static void postDeleted(int position, int _numberOfCallback) {
            postFeaturesCallbackList.get(_numberOfCallback).onPostDeleted(position);
        }

    }

    public static class FeedRefresh {

        private static FeedRefresh instance = null;
        private static List<FeedRefreshCallback> feedRefreshCallbackList;

        public FeedRefresh() {
            feedRefreshCallbackList = new ArrayList<FeedRefreshCallback>();
        }

        public static FeedRefresh getInstance() {
            if (instance == null)
                instance = new FeedRefresh();

            return instance;
        }

        public static void setFeedRefreshCallback(FeedRefreshCallback feedRefreshCallback) {
            feedRefreshCallbackList.add(feedRefreshCallback);
        }

        public static void feedRefreshStart() {
            if (instance != null) {
                for (int i = 0; i < feedRefreshCallbackList.size(); i++) {
                    feedRefreshCallbackList.get(i).onFeedRefresh();
                }
            }
        }

    }


    public static class Utils {

        public static final String calculateDistance(Double distance) {
            String distanceValue;
            if (distance < 1) {
                distance = distance * 1000;
                distanceValue = distance.intValue() + "m";
            } else {
                distanceValue = String.format("%.2f", distance) + "km";
            }
            return distanceValue;
        }


    }

}
