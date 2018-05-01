package com.example.android.inspire3.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lama on 9/28/2017 AD.
 */

public final class PostsContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.inspire3";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_POSTS = "products";

    private PostsContract() {
    }

    public static abstract class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "posts";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "description";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_DATE = "date_created";
        public static final String COLUMN_IMAGE = "image";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_POSTS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_POSTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_POSTS;
    }
}
