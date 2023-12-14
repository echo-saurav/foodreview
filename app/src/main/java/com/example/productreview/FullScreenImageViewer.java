package com.example.productreview;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class FullScreenImageViewer extends AppCompatActivity {
    PhotoView photoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer_activity);
        photoView=findViewById(R.id.photo_viewer);

        String imageUrl=getIntent().getStringExtra("imageUrl");
        if(imageUrl!=null){
            Picasso.get()
                    .load(getIntent().getStringExtra("imageUrl"))
                    .into(photoView);
        }
    }
}
