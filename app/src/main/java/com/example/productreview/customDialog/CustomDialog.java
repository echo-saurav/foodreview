package com.example.productreview.customDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.productreview.R;

public class CustomDialog extends AlertDialog {
    private TextView header, subText;
    private Button yes,no;
    private customDialogOnClickListener customDialogOnClickListener;
    private String headerString,subTextString;


    public CustomDialog(@NonNull Context context, String headerString, String subTextString, customDialogOnClickListener customDialogOnClickListener) {
        super(context);
        this.customDialogOnClickListener=customDialogOnClickListener;
        this.subTextString=subTextString;
        this.headerString=headerString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        header=findViewById(R.id.header);
        subText =findViewById(R.id.subtext);
        header.setText(headerString);
        subText.setText(subTextString);

        yes=findViewById(R.id.yes);
        no=findViewById(R.id.no);
        yes.setOnClickListener(view -> {
            customDialogOnClickListener.onYesClick();
            dismiss();
        });
        //
        no.setOnClickListener(view -> {
            customDialogOnClickListener.onNoClick();
            dismiss();
        });

    }

    public interface customDialogOnClickListener{
        void onYesClick();
        void onNoClick();
    }
}
