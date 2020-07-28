package com.example.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textViewDone;
    private GalleryListAdapter galleryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        init();
    }

    private void init() {
        textViewDone = findViewById(R.id.textViewDone);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        getAllMedia();

        onClickDone();
    }

    /**
     * Click on done button
     */
    private void onClickDone() {

        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryListAdapter != null) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("list"
                            , galleryListAdapter.getSelectedList());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /**
     * Get all media from gallery
     */
    private void getAllMedia() {
        ArrayList<GalleryData> galleryList = new ArrayList<>();
        String[] columns = new String[]{MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE,
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        final String orderBy = MediaStore.Files.FileColumns.DATE_ADDED;
        Uri queryUri = MediaStore.Files.getContentUri("external");

        try {
            Cursor imageCursor = getContentResolver()
                    .query(queryUri,
                            columns,
                            selection,
                            null, // Selection args (none).
                            orderBy + " DESC" // Sort order.
                    );

            if (imageCursor != null) {
                int imageColumnIndex = imageCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
                int count = imageCursor.getCount();


                for (int i = 0; i < count; i++) {
                    imageCursor.moveToPosition(i);
                    int id = imageCursor.getInt(imageColumnIndex);
                    int dataColumnIndex = imageCursor.getColumnIndex(
                            MediaStore.Files.FileColumns.DATA);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inSampleSize = 4;
                    bmOptions.inPurgeable = true;
                    int type = imageCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
                    String file = imageCursor.getString(dataColumnIndex);
                    int tempType = imageCursor.getInt(type);

                    galleryList.add(new GalleryData(file, tempType + "", false));
                }
                imageCursor.close();
                setAdapter(galleryList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set adapter
     */
    private void setAdapter(ArrayList<GalleryData> list) {
        recyclerView.setAdapter(galleryListAdapter = new GalleryListAdapter(
                GalleryActivity.this, list));
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
