package com.uren.catchu.Constants;

public class NumericConstants {

    //Bitmap circular constants
    public static final int friendImageShown = 500;

    public static final int GROUP_NAME_MAX_LENGTH = 25;

    public static final int CODE_ADD_VALUE = 0;
    public static final int CODE_REMOVE_VALUE = 1;
    public static final int CODE_CHANGE_VALUE = 2;
    public static final int RESPONSE_OK = 1;

    //Share video properties
    public static final int MAX_VIDEO_DURATION = 15;
    public static final int MAX_VIDEO_SIZE_IN_MB =  25;
    public static final long MAX_VIDEO_SIZE =  209715200L;
    public static final int SHARE_VIDEO_HEIGHT = 176;
    public static final int SHARE_VIDEO_WIDHT = 144;

    //public static final long MAX_IMAGE_SIZE =  10485760L;
    public static final int MAX_IMAGE_SIZE_1MB =  1048576;
    public static final int MAX_IMAGE_SIZE_1ANDHALFMB =  1572864;
    public static final int MAX_IMAGE_SIZE_2MB =  2097152;
    public static final int MAX_IMAGE_SIZE_2ANDHALFMB =  2621440;
    public static final int MAX_IMAGE_SIZE_3MB =  3145728;
    public static final int MAX_IMAGE_SIZE_5MB = 5242880;

    public static final int IMAGE_RESOLUTION_480 =  480;
    public static final int IMAGE_RESOLUTION_640 =  640;
    public static final int IMAGE_RESOLUTION_800 =  800;

    //DialogBox Results
    public static final int UPDATE_RESULT_OK = 1;
    public static final int UPDATE_RESULT_FAIL = 0;

    //Photo Chosen items
    public static final int CODE_GALLERY_POSITION = 0;
    public static final int CODE_CAMERA_POSITION = 1;
    public static final int CODE_SCREENSHOT_POSITION = 1;
    public static final int CODE_PHOTO_REMOVE = 2;
    public static final int CODE_VIDEO_REMOVE = 2;
    public static final int CODE_PHOTO_EDIT = 3;
    public static final int CODE_PLAY_VIDEO = 3;

    //Phone num verify duration
    public static final int VERIFY_PHONE_NUM_DURATION = 60;

    //Feed paignation initial values
    public static final int DEFAULT_FEED_PAGE_COUNT = 1;
    public static final int DEFAULT_FEED_PERPAGE_COUNT = 15; // EN AZ 4 OLMALI.
    public static final int DEFAULT_FEED_RADIUS = 5000; //metre cinsinden
    public static int FILTERED_FEED_RADIUS = 5000; //metre cinsinden

    //Share type values
    public static final int CODE_PUBLIC_SHARED = 0;
    public static final int CODE_FRIEND_SHARED = 1;
    public static final int CODE_GROUP_SHARED = 2;
    public static final int CODE_JUSTME_SHARED = 3;

    public static final int SHARE_TRY_COUNT = 2;

    //Select unselect all constants
    public static final int CODE_SELECT_ALL = 0;
    public static final int CODE_UNSELECT_ALL = 1;

    //Share
    public static final int REQUEST_CODE_ENABLE_LOCATION = 3003;

    //User posts gridView pagination
    public static final int DEFAULT_PROFILE_GRIDVIEW_PAGE_COUNT = 1;
    public static final int DEFAULT_PROFILE_GRIDVIEW_PERPAGE_COUNT = 30; // EN AZ 4 OLMALI.

    //USER POST VIEW TYPE
    public static final int USER_POST_VIEW_TYPE_GRID = 0;
    public static final int USER_POST_VIEW_TYPE_LIST = 1;

    //Post More Dialog box codes
    public static final int CODE_UNFOLLOW_POSITION = 0;
    public static final int CODE_DISABLE_COMMENTS_POSITION = 1;
    public static final int CODE_REPORT_POSITION = 2;
    public static final int CODE_DELETE_POST = 3;

    public static final int MESSAGE_LIMIT_COUNT = 25;
    public static final int REC_MAXITEM_LIMIT_COUNT = 15;

    public final static int KEYBOARD_CHECK_VALUE = 200;

    public final static int FCM_MAX_MESSAGE_LEN = 30;
    public final static int MAX_ALLOWED_NOTIFICATION_SIZE = 4;


}
