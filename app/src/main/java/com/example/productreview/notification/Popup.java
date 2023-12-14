package com.example.productreview.notification;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.productreview.R;

public class Popup extends Activity {

    TextView title,message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.popup);
        setFinishOnTouchOutside(false);
        title=findViewById(R.id.title);
        message=findViewById(R.id.message);
        title.setText(getIntent().getStringExtra("title"));
        message.setText(getIntent().getStringExtra("message"));

    }
}
