package com.theoffice.moneysaver.utils;

import okhttp3.MediaType;

public final class AppConstants {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final String BASE_URL = "https://money-saver.vercel.app";
    public static final String GOALS_URL = "/api/goals";
    public static final String ADD_GOAL_URL = "/api/addGoal";
    public static final String ADD_CONTRIBUTION = "/api/addContribution";
    public static final String VALIDATE_USER_URL = "/api/validateUserId";
    public static final String GET_GOAL_CONTRIBUTIONS = "/api/contributions";
    public static final String CREATE_USER_URL = "/api/addUser";
    public static final String MONEY_SAVER_ERROR = "Error";
    public static final int HUAWEI_LOGIN_CODE = 8888;
    public static final String GOAL_NAME = "NAME";
    public static final String GOAL_DATE = "DATE";
    public static final String GOAL_VALUE = "VALUE";
    public static final String GOAL_PHOTO = "PHOTO";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final int MY_HOME = 0;
    public static final int MY_PROFILE = 1;
    public static final int MY_GOALS = 2;
    public static final int RV_LIST_VIEW = 1;
    public static final int RV_GRID_VIEW = 2;
    public static final int PERMISSION_REQUEST = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final String USER_PLACEHOLDER = "https://money-saver.vercel.app/img/user.png";
    public static final String CLOUDINARY_NAME = "alexanderc12";
    public static final String DATE_FORMAT_TO_SHOW = "LLLL dd, yyyy";
    public static final String ADD_USER_PUSH_TOKEN_URL = "/api/addToken";
}
