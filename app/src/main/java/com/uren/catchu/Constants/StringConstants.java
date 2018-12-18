package com.uren.catchu.Constants;

public class StringConstants {

    public static final String APP_NAME = "CatchU";
    public static final String SPACE_VALUE = " ";

    //image display type
    public static final String displayRounded = "rounded";
    public static final String displayRectangle = "rectangle";

    //Cache file directories
    public static String friendsCacheDirectory = "Friends";
    public static String groupsCacheDirectory = "Groups";

    //Viewpager show types
    public static final String horizontalShown = "horizontal";
    public static final String verticalShown = "vertical";
    public static final String gridShown = "grid";

    public static final String propFriends = "Friends";
    public static final String propPersons = "Persons";
    public static final String propOnlyMe = "OnlyMe";
    public static final String propGroups = "Groups";

    //Intent variables
    public static final String PUTEXTRA_GROUP_ID = "GROUP_ID";
    public static final String PUTEXTRA_GROUP_NAME = "GROUP_NAME";
    public static final String PUTEXTRA_ACTIVITY_NAME = "ACTIVITY_NAME";
    public static final String PUTEXTRA_SHARE_FRIEND_COUNT = "SHARE_FRIEND_COUNT";
    public static final String PUTEXTRA_SHARE_GROUP_COUNT = "SHARE_GROUP_COUNT";
    public static final String PUTEXTRA_PHONE_NUM = "PHONE_NUM";

    //Group Request Types
    public static final String GET_AUTHENTICATED_USER_GROUP_LIST = "GET_AUTHENTICATED_USER_GROUP_LIST"; //Kullanicinin dahil oldugu gruplar
    public static final String GET_GROUP_PARTICIPANT_LIST = "GET_GROUP_PARTICIPANT_LIST"; //Bir grubun katilimcilarini cekmek icin
    public static final String CREATE_GROUP = "CREATE_GROUP";
    public static final String EXIT_GROUP = "EXIT_GROUP";
    public static final String ADD_PARTICIPANT_INTO_GROUP = "ADD_PARTICIPANT_INTO_GROUP";
    public static final String CHANGE_GROUP_ADMIN = "CHANGE_GROUP_ADMIN";
    public static final String UPDATE_GROUP_INFO = "UPDATE_GROUP_INFO";

    //Friend Request Types
    public static final String FRIEND_FOLLOW_REQUEST = "followRequest";
    public static final String FRIEND_ACCEPT_REQUEST = "acceptRequest";
    public static final String FRIEND_GET_REQUESTING_FOLLOW_LIST = "requestingFollowList";
    public static final String FRIEND_CREATE_FOLLOW_DIRECTLY = "createFollowDirectly";
    public static final String FRIEND_DELETE_FOLLOW = "deleteFollow";
    public static final String FRIEND_DELETE_PENDING_FOLLOW_REQUEST = "deletePendingFollowRequest";


    //Share privacy types
    public static final String SHARE_TYPE_EVERYONE = "EVERYONE";
    public static final String SHARE_TYPE_ALL_FOLLOWERS = "ALL_FOLLOWERS";
    public static final String SHARE_TYPE_CUSTOM = "CUSTOM";
    public static final String SHARE_TYPE_SELF = "SELF";
    public static final String SHARE_TYPE_GROUP = "GROUP";

    //Animation Tags
    public static final String ANIMATE_LEFT_TO_RIGHT = "ANIMATE_LEFT_TO_RIGHT";
    public static final String ANIMATE_RIGHT_TO_LEFT = "ANIMATE_RIGHT_TO_LEFT";
    public static final String ANIMATE_DOWN_TO_UP = "ANIMATE_DOWN_TO_UP";
    public static final String ANIMATE_UP_TO_DOWN = "ANIMATE_UP_TO_DOWN";

    //Image Extension Types
    public static final String JPG_TYPE = "jpg";
    public static final String MP4_TYPE = "mp4";

    public static final String CAMERA_TEXT = "CAMERA";
    public static final String GALLERY_TEXT = "GALLERY";
    public static final String FROM_FILE_TEXT = "FROM_FILE";

    //UserProfile Request Types
    public static final String USER_PROFILE_UPDATE = "USER_PROFILE_UPDATE";

    //Follow Request Types
    public static final String GET_USER_FOLLOWERS = "followers";
    public static final String GET_USER_FOLLOWINGS = "followings";

    // Upload media types
    public static final String VIDEO_TYPE = "video";
    public static final String IMAGE_TYPE = "image";

    //Image load types for feed viewPager
    public static final String VIEWPAGER_VIDEO = "VIEWPAGER_VIDEO";
    public static final String VIEWPAGER_IMAGE = "VIEWPAGER_IMAGE";

    //Provider Types
    public static final String PROVIDER_TYPE_FACEBOOK = "facebook";
    public static final String PROVIDER_TYPE_TWITTER = "twitter";
    public static final String PROVIDER_TYPE_PHONE = "phone";

    //Follow Status
    public static final String FOLLOW_STATUS_FOLLOWING = "FOLLOWING";
    public static final String FOLLOW_STATUS_PENDING = "PENDING";
    public static final String FOLLOW_STATUS_OWN = "OWN";
    public static final String FOLLOW_STATUS_NONE = "NONE";

    //Person list fragment comingFor values
    public static final String COMING_FOR_LIKE_LIST = "LIKE_LIST";

    public static final String APP_GOOGLE_PLAY_DEFAULT_LINK = "https://play.google.com/store/apps/details?id=";
    //AWS null representer
    public static final String AWS_EMPTY = "empty";

    //Dynamic Link constants
    public static final String DYNAMIC_LINK_DOMAIN = "f2wrp.app.goo.gl";

    //Report problem constants
    public static final String REPORT_PROBLEM_TYPE_BUG = "bug";
    public static final String REPORT_PROBLEM_TYPE_INAPPROPIATE = "inappropiate ";
    public static final String REPORT_PROBLEM_TYPE_SPAM = "spam ";
    public static final String REPORT_PROBLEM_TYPE_FEEDBACK = "feedback ";


    //Character constants
    public static final String CHAR_AMPERSAND = "@";

    public static final String GROUP_OP_CHOOSE_TYPE = "CHOOSE";
    public static final String GROUP_OP_VIEW_TYPE = "VIEW";

    public static final String CREATE_AT_NOW = "NOW";

    //FEED TYPES
    public static final String FEED_TYPE_PUBLIC = "public";
    public static final String FEED_TYPE_CATCH = "catch";

    //PROFILE POST TYPE
    public static final String PROFILE_POST_TYPE_SHARED = "shared";
    public static final String PROFILE_POST_TYPE_CAUGHT = "caught";
    public static final String PROFILE_POST_TYPE_GROUP = "group";
    public static final String USER_POST_VIEW_TYPE_GRID= "gridview";
    public static final String USER_POST_VIEW_TYPE_LIST = "listview";
    public static final String OTHER_PROFILE_POST_TYPE_SHARED = "otherShared";

    //FIrebase messaging constants
    public static final String FB_CHILD_MESSAGES = "Messages";
    public static final String FB_CHILD_WITH_PERSON = "WithPerson";
    public static final String FB_CHILD_WITH_GROUP = "WithGroup";
    public static final String FB_CHILD_DATE = "Date";
    public static final String FB_CHILD_MESSAGE = "Message";
    public static final String FB_CHILD_SENDER = "Sender";
    public static final String FB_CHILD_RECEIPT = "Receipt";
    public static final String FB_CHILD_NAME = "Name";
    public static final String FB_CHILD_USERID = "UserId";

    public static final String FB_CHILD_ERRORS = "Errors";
    public static final String FB_CHILD_ANDROID = "Android";



}
