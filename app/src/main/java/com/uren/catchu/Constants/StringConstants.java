package com.uren.catchu.Constants;

public class StringConstants {

    public static final String defSpace = " ";

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

    //Animation Tags
    public static final String AnimateLeftToRight = "AnimateLeftToRight";
    public static final String AnimateRightToLeft = "AnimateRightToLeft";

    //Image Extension Types
    public static final String JPG_TYPE = "jpg";

    public static final String CAMERA_TEXT = "CAMERA";
    public static final String GALLERY_TEXT = "GALLERY";


}
