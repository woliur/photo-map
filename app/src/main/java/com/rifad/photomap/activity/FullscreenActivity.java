package com.rifad.photomap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rifad.photomap.R;
import com.rifad.photomap.data.constant.AppConstants;
import com.rifad.photomap.model.Photo;

public class FullscreenActivity extends AppCompatActivity {

    // variable
    private Context mContext;
    private Activity mActivity;
    private Photo photo;

    // ui elements
    private ImageView ivPhoto;
    private FloatingActionButton fabMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initListener();
    }

    private void initVariable() {
        mContext = FullscreenActivity.this;
        mActivity = FullscreenActivity.this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(AppConstants.KEY_PHOTO)) {
            photo = bundle.getParcelable(AppConstants.KEY_PHOTO);
        }
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_fullscreen);

        // put back arrow on toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // ui reference
        ivPhoto = findViewById(R.id.ivPhoto);
        fabMap = findViewById(R.id.fabMap);

        // load photo
        Glide.with(mContext)
                .load(photo.getPath())
                .into(ivPhoto);
    }

    private void initListener() {
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, MapsActivity.class)
                        .putExtra(AppConstants.KEY_PHOTO, photo));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
