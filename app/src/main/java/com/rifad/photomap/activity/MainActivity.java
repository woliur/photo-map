package com.rifad.photomap.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rifad.photomap.R;
import com.rifad.photomap.adapter.PhotoGridAdapter;
import com.rifad.photomap.data.constant.AppConstants;
import com.rifad.photomap.listener.OnItemClickListener;
import com.rifad.photomap.model.Photo;
import com.rifad.photomap.data.sqlite.PhotoDbController;
import com.rifad.photomap.util.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // variable
    private Context mContext;
    private Activity mActivity;
    private RequestManager glide;
    private PhotoGridAdapter photoGridAdapter;
    private ArrayList<Photo> photos = new ArrayList<>();
    private String mCurrentPhotoPath;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 358;
    private double latitude;
    private double longitude;
    private PhotoDbController dbController;

    // constant
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GRID_COLUMN_COUNT = 3;

    // ui element
    private RecyclerView rvPhotoGrid;
    private ImageButton btnCapture;
    private TextView tvNoPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initListener();
    }

    private void initVariable() {
        mContext = MainActivity.this;
        mActivity = MainActivity.this;
        glide = Glide.with(mContext);
        photoGridAdapter = new PhotoGridAdapter(glide, photos);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        dbController = new PhotoDbController(mContext);

        // load photos from database
        photos.addAll(dbController.getAllPhotos());
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_main);

        // ui reference
        rvPhotoGrid = findViewById(R.id.rvPhotoGrid);
        btnCapture = findViewById(R.id.btnCapture);
        tvNoPhoto = findViewById(R.id.tvNoPhoto);

        // init RecyclerView
        rvPhotoGrid.setHasFixedSize(true);
        rvPhotoGrid.setLayoutManager(new GridLayoutManager(mContext, GRID_COLUMN_COUNT));
        rvPhotoGrid.setAdapter(photoGridAdapter);

        // if photos added hide message
        if (!photos.isEmpty()) {
            hideMessage();
        }
    }

    private void initListener() {
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndCapture();
            }
        });

        photoGridAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(mActivity, FullscreenActivity.class)
                        .putExtra(AppConstants.KEY_PHOTO, photos.get(position)));
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.rifad.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("EN", "US")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void checkPermissionAndCapture() {

        if (LocationUtil.isLocationEnabled(mContext)) {
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSIONS_REQUEST_CODE);
            } else {
                dispatchTakePictureIntent();
            }
        } else {
            Toast.makeText(mContext, "Location is Turned off!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImageWithLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Photo photo = new Photo(mCurrentPhotoPath, latitude, longitude);
                    photos.add(0, photo);
                    photoGridAdapter.notifyItemInserted(0);

                    dbController.addPhoto(photo);

                    hideMessage();
                } else {
                    Toast.makeText(mContext, "Location is NULL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void hideMessage() {
        if (tvNoPhoto.getVisibility() == View.VISIBLE) {
            tvNoPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // get the location
            saveImageWithLocation();

            // make photo visible in gallery app
            galleryAddPic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(mContext, "Location Permission is Required!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
