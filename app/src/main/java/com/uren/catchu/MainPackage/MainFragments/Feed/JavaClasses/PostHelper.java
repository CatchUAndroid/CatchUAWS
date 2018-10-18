package com.uren.catchu.MainPackage.MainFragments.Feed.JavaClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.ApiGatewayFunctions.PostLikeProcess;
import com.uren.catchu.ApiGatewayFunctions.PostListResponseProcess;
import com.uren.catchu.GeneralUtils.CommonUtils;
import com.uren.catchu.MainPackage.MainFragments.BaseFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities.ImageActivity;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubActivities.VideoActivity;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.MapFragment;
import com.uren.catchu.MainPackage.MainFragments.Feed.SubFragments.PersonListFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.JavaClasses.FollowInfoListItem;
import com.uren.catchu.MainPackage.MainFragments.Profile.ProfileFragment;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.OtherProfileFragment;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

import catchu.model.BaseRequest;
import catchu.model.BaseResponse;
import catchu.model.FollowInfoResultArrayItem;
import catchu.model.Media;
import catchu.model.Post;
import catchu.model.PostListResponse;
import catchu.model.User;

import static com.uren.catchu.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static com.uren.catchu.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static com.uren.catchu.Constants.StringConstants.COMING_FOR_LIKE_LIST;
import static com.uren.catchu.Constants.StringConstants.IMAGE_TYPE;
import static com.uren.catchu.Constants.StringConstants.VIDEO_TYPE;

public class PostHelper {

    public static class LikeClicked {

        static Post post;
        static boolean isPostLiked;

        public static final void startProcess(Context context1, Post post1, boolean isPostLiked1) {
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
                fragmentNavigation.pushFragment(PersonListFragment.newInstance(toolbarTitle, postId, comingFor), ANIMATE_DOWN_TO_UP);
            }
        }


    }

    public static class CommentListClicked {

        static BaseFragment.FragmentNavigation fragmentNavigation;
        static String toolbarTitle;
        static String postId;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav, String toolbarTitle,
                                              String postId) {

            fragmentNavigation = fragmNav;
            CommentListClicked.toolbarTitle = toolbarTitle;
            CommentListClicked.postId = postId;

            CommentListClicked commentListClicked = new CommentListClicked(context);
        }

        private CommentListClicked(Context context) {
            postCommentListClickedProcess(context);
        }

        private void postCommentListClickedProcess(Context context) {
            if (fragmentNavigation != null) {
                //fragmentNavigation.pushFragment(PersonListFragment.newInstance(toolbarTitle, postId), ANIMATE_DOWN_TO_UP);
            }
        }

    }

    public static class ViewPagerItemClicked {

        static Media media;

        public static final void startProcess(Activity activity, Context context, Media m) {

            media = m;
            ViewPagerItemClicked viewPagerItemClicked = new ViewPagerItemClicked(activity, context);
        }

        private ViewPagerItemClicked(Activity activity, Context context) {

            showItemInFullView(activity, media);

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
        static FollowInfoListItem followInfoListItem;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav, FollowInfoListItem followInfoListItem) {

            fragmentNavigation = fragmNav;
            ProfileClicked.followInfoListItem = followInfoListItem;

            ProfileClicked commentListClicked = new ProfileClicked(context);
        }

        private ProfileClicked(Context context) {
            postProfileClickedProcess(context);
        }

        private void postProfileClickedProcess(Context context) {
            if (fragmentNavigation != null) {
                if (followInfoListItem.getResultArrayItem().getUserid().equals(AccountHolderInfo.getUserID())) {
                    //clicked own profile
                    fragmentNavigation.pushFragment(ProfileFragment.newInstance(false), ANIMATE_RIGHT_TO_LEFT);
                } else {
                    //clicked others profile
                    fragmentNavigation.pushFragment(OtherProfileFragment.newInstance(followInfoListItem), ANIMATE_RIGHT_TO_LEFT);
                }
            }
        }

    }

    public static class LocatonDetailClicked {

        static BaseFragment.FragmentNavigation fragmentNavigation;
        static Post post;
        static ImageView imgProfilePic;

        public static final void startProcess(Context context, BaseFragment.FragmentNavigation fragmNav, Post post, ImageView imgProfilePic) {

            fragmentNavigation = fragmNav;
            LocatonDetailClicked.post = post;
            LocatonDetailClicked.imgProfilePic = imgProfilePic;


            LocatonDetailClicked locatonDetailClicked = new LocatonDetailClicked(context);
        }

        private LocatonDetailClicked(Context context) {
            postLocationDetailClickedProcess(context);
        }

        private void postLocationDetailClickedProcess(final Context context) {
            if (fragmentNavigation != null) {
                CommonUtils.showToast(context, "lokasyon detayı tıklandı..");

                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.half_map_view);
                dialog.show();

                MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
                MapsInitializer.initialize(context);

                mMapView = (MapView) dialog.findViewById(R.id.mapView);
                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();// needed to get the map to display immediately
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        GoogleMap mMap =googleMap;
                        BigDecimal latitude = post.getLocation().getLatitude();
                        BigDecimal longitude = post.getLocation().getLongitude();
                        Log.i("lattitude", String.valueOf(latitude.doubleValue()) );
                        Log.i("longitude", String.valueOf(longitude.doubleValue()) );

                        LatLng latLng = new LatLng( latitude.doubleValue(), longitude.doubleValue());

                        Marker marker;
                        MarkerOptions options = new MarkerOptions().position(latLng);
                        Bitmap bitmap = createUserBitmap(context);
                        if(bitmap!=null){
                            options.title(post.getUser().getUsername());
                            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                            options.anchor(0.5f, 0.907f);
                            marker = mMap.addMarker(options);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        }


                    }
                });



            }
        }


        private Bitmap createUserBitmap(Context context) {
            Bitmap result = null;
            try {
                result = Bitmap.createBitmap(dp(62, context), dp(76, context), Bitmap.Config.ARGB_8888);
                result.eraseColor(Color.TRANSPARENT);
                Canvas canvas = new Canvas(result);
                Drawable drawable = context.getResources().getDrawable(R.mipmap.deleted_livepin);
                drawable.setBounds(0, 0, dp(62, context), dp(76, context));
                drawable.draw(canvas);

                Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                RectF bitmapRect = new RectF();
                canvas.save();



                Bitmap bitmap = ((BitmapDrawable)imgProfilePic.getDrawable()).getBitmap();
                //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
                if (bitmap != null) {
                    BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Matrix matrix = new Matrix();
                    float scale = dp(52, context) / (float) bitmap.getWidth();
                    matrix.postTranslate(dp(5, context), dp(5, context));
                    matrix.postScale(scale, scale);
                    roundPaint.setShader(shader);
                    shader.setLocalMatrix(matrix);
                    bitmapRect.set(dp(5, context), dp(5, context), dp(52 + 5, context), dp(52 + 5, context));
                    canvas.drawRoundRect(bitmapRect, dp(26, context), dp(26, context), roundPaint);
                }
                canvas.restore();
                try {
                    canvas.setBitmap(null);
                } catch (Exception e) {}
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return result;
        }

        public int dp(float value, Context context) {
            if (value == 0) {
                return 0;
            }
            return (int) Math.ceil(context.getResources().getDisplayMetrics().density * value);
        }

    }


}