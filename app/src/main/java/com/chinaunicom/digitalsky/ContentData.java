package com.chinaunicom.digitalsky;

import android.net.Uri;

/**
 * 常量类
 */
public class ContentData {
    public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir";
    public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item";
    public static final String MIME_ITEM = "com.chinaunicom.digitalsky.accounts";
    public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MIME_ITEM ;
    public static final String MIME_TYPE_MULTIPLE = MIME_DIR_PREFIX + "/" + MIME_ITEM ;

    public static final String AUTHORITY = "com.chinaunicom.digitalsky.accountprovider";
    public static final String PATH_SINGLE = "accounts/#";
    public static final String PATH_MULTIPLE = "accounts";
    public static final String URL = "content://" + AUTHORITY + "/" + PATH_MULTIPLE;
    public static final Uri CONTENT_URI = Uri.parse(URL);   //暴露和共享的URL

    /**
     * 数据库表的字段
     */
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE_NUMBER = "phone";
    public static final String KEY_PASSWORD = "password";

    /**
     * SMSSDK获取短信验证码
     */
    public static final String AppKey = "2858b7ab8efde";
    public static final String AppSECRET = "ac903bf2120ebe958a065bacb8ba86a5";
}
