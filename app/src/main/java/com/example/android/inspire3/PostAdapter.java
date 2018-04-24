package com.example.android.inspire3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inspire3.data.PostsContract.PostEntry;

/**
 * Created by lama on 9/28/2017 AD.
 */

public class PostAdapter extends CursorAdapter {
    public PostAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTV = view.findViewById(R.id.name);
        TextView tagTV = view.findViewById(R.id.tag);
        ImageView image = view.findViewById(R.id.image);
        Button shareButton = view.findViewById(R.id.share_button);
        String name = cursor.getString(cursor.getColumnIndex(PostEntry.COLUMN_NAME));
        String tag = cursor.getString(cursor.getColumnIndex(PostEntry.COLUMN_TAG));
        final byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(PostEntry.COLUMN_IMAGE));
        if (imageBytes != null)
            image.setImageBitmap(DbBitmapUtility.getImage(imageBytes));
        // final long id = cursor.getLong(cursor.getColumnIndex(PostEntry._ID));
        nameTV.setText(name);
        tagTV.setText(tag);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (intent != null)
                {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setPackage("com.instagram.android");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), DbBitmapUtility.getImage(imageBytes), "I am Happy", "Share happy !")));

                    shareIntent.setType("image/jpeg");

                    context.startActivity(shareIntent);
                }
                else
                {
                    // bring user to the market to download the app.
                    // or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
                    context.startActivity(intent);
                }
            }
        });
    }
}
