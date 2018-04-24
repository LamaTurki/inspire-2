package com.example.android.inspire3.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.inspire3.data.PostsContract.PostEntry;

/**
 * Created by lama on 9/28/2017 AD.
 */

public class PostProvider extends ContentProvider {
    public static final String LOG_TAG = PostProvider.class.getSimpleName();
    public static final int POSTS = 100;
    public static final int POSTS_ID = 101;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(PostsContract.CONTENT_AUTHORITY, PostsContract.PATH_POSTS, POSTS);
        mUriMatcher.addURI(PostsContract.CONTENT_AUTHORITY, PostsContract.PATH_POSTS + "/#", POSTS_ID);
    }

    PostsDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PostsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = mUriMatcher.match(uri);
        switch (match) {
            case POSTS:
                cursor = database.query(PostEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case POSTS_ID:
                selection = PostEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PostEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case POSTS:
                String name = values.getAsString(PostEntry.COLUMN_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Product requires a name");
                }
                String tag = values.getAsString(PostEntry.COLUMN_TAG);
                if (tag == null) {
                    throw new IllegalArgumentException("Product requires a tag");
                }

                SQLiteDatabase database = mDbHelper.getWritableDatabase();

                long id = database.insert(PostEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case POSTS:
                return updatePost(uri, values, selection, selectionArgs);
            case POSTS_ID:
                selection = PostEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePost(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePost(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(PostEntry.COLUMN_NAME)) {
            String name = values.getAsString(PostEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Post requires a description");
            }}
            if (values.containsKey(PostEntry.COLUMN_TAG)) {
                String tag = values.getAsString(PostEntry.COLUMN_TAG);
                if (tag == null) {
                    throw new IllegalArgumentException("Post requires a tag");
                }
            }
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            getContext().getContentResolver().notifyChange(uri, null);
            int rowsUpdated = db.update(PostEntry.TABLE_NAME, values, selection, selectionArgs);

            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
        }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case POSTS:
                rowsDeleted = database.delete(PostEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POSTS_ID:
                selection = PostEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PostEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case POSTS:
                return PostEntry.CONTENT_LIST_TYPE;
            case POSTS_ID:
                return PostEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

