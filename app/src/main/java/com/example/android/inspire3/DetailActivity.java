package com.example.android.inspire3;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inspire3.data.PostsContract.PostEntry;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {
    Uri mUri;
    Bitmap mBitmap = null;
    EditText nameEditText;
    ImageView imageView;
    Spinner spin;
    ArrayAdapter aa;
    Button selectImageButton;
    String tag;
    String[] tags = {"Work", "Study", "Nature", "Food", "Sport", "Fashion"};
    private int PICK_IMAGE_REQUEST = 1;
    private boolean mDataHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDataHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        spin = (Spinner) findViewById(R.id.spinner1);
        spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tags);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        nameEditText = (EditText) findViewById(R.id.edit_name);
        imageView = (ImageView) findViewById(R.id.imageview);
        selectImageButton = (Button) findViewById(R.id.select_image_button);
        mUri = getIntent().getData();
        if (mUri == null) {
            setTitle(getString(R.string.detail_activity_title_add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.detail_activity_title_edit));
            getSupportLoaderManager().initLoader(0, null, this);
        }

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //resource: http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        nameEditText.setOnTouchListener(mTouchListener);
        spin.setOnTouchListener(mTouchListener);
        selectImageButton.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_delete_item:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_save:
                saveItemAndfinish();
                return true;
            case android.R.id.home:
                if (!mDataHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveItemAndfinish() {
        ContentValues values = new ContentValues();
        String name = nameEditText.getText().toString().trim();
        String tag = spin.getSelectedItem().toString().trim();
        if (mUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(tag) && mBitmap == null)
            return;
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.missing_post_desc),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(this, getString(R.string.missing_post_tag),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mUri == null && mBitmap == null) {
            Toast.makeText(this, R.string.missing_post_image,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(PostEntry.COLUMN_NAME, name);
        values.put(PostEntry.COLUMN_TAG, tag);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        values.put(PostEntry.COLUMN_DATE, dateFormat.format(date));
        if (mBitmap != null)
            values.put(PostEntry.COLUMN_IMAGE, DbBitmapUtility.getBytes(mBitmap));
        if (mUri == null) {
            Uri newUri = getContentResolver().insert(PostEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_with_saving_post),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.successful_insertion),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowUpdated = getContentResolver().update(mUri, values, null, null);
            if (rowUpdated == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.failed_update,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.successful_update,
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.yes, discardButtonClickListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onBackPressed() {
        if (!mDataHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        int rowDeleted = getContentResolver().delete(mUri, null, null);
        if (rowDeleted == 0) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, R.string.failed_delete,
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, R.string.successful_delete,
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // resource: http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(mBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PostEntry._ID,
                PostEntry.COLUMN_NAME,
                PostEntry.COLUMN_TAG,
                PostEntry.COLUMN_DATE,
                PostEntry.COLUMN_IMAGE};
        return new CursorLoader(this, mUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            nameEditText.setText(data.getString(data.getColumnIndex(PostEntry.COLUMN_NAME)));
            spin.setSelection(aa.getPosition(data.getString(data.getColumnIndex(PostEntry.COLUMN_TAG))));
            byte[] imageBytes = data.getBlob(data.getColumnIndex(PostEntry.COLUMN_IMAGE));
            if (imageBytes != null)
                imageView.setImageBitmap(DbBitmapUtility.getImage(imageBytes));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        spin.setSelection(0);
        imageView.setImageResource(0);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        tag = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        tag = "Work";
    }
}
