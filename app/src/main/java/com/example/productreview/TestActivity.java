package com.example.productreview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import jp.wasabeef.richeditor.RichEditor;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
    }

    //    private RichEditor mEditor;
//    private TextView mPreview;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test_activity);
//        mEditor = (RichEditor) findViewById(R.id.editor);
//        mEditor.setEditorHeight(200);
//        mEditor.setEditorFontSize(22);
//        mEditor.setEditorFontColor(Color.RED);
//        //mEditor.setEditorBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundColor(Color.BLUE);
//        //mEditor.setBackgroundResource(R.drawable.bg);
//        mEditor.setPadding(10, 10, 10, 10);
//        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
//        mEditor.setPlaceholder("Insert text here...");
//        //mEditor.setInputEnabled(false);
//
//
//        mPreview = findViewById(R.id.preview);
//        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
//            @Override
//            public void onTextChange(String text) {
//                mPreview.setText(text);
//            }
//        });
//
//        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.undo();
//            }
//        });
//
//        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.redo();
//            }
//        });
//
//        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setBold();
//            }
//        });
//
//        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setItalic();
//            }
//        });
//
//
//        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setUnderline();
//            }
//        });
//
//        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setHeading(1);
//            }
//        });
//
//        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setHeading(2);
//            }
//        });
//
//        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setHeading(3);
//            }
//        });
//
//
//        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
//            private boolean isChanged;
//
//            @Override
//            public void onClick(View v) {
//                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
//                isChanged = !isChanged;
//            }
//        });
//
//        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
//            private boolean isChanged;
//
//            @Override
//            public void onClick(View v) {
//                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
//                isChanged = !isChanged;
//            }
//        });
//
//
//        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setAlignLeft();
//            }
//        });
//
//        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setAlignCenter();
//            }
//        });
//
//        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setAlignRight();
//            }
//        });
//
//
//        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setBullets();
//            }
//        });
//
//        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.setNumbers();
//            }
//        });
//
//        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
//                        "dachshund");
//            }
//        });
//
//        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
//            }
//        });
//        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.insertTodo();
//            }
//        });
//
//    }
//
}
