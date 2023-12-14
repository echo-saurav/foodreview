package com.example.productreview.userManagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.productreview.Helper;
import com.example.productreview.R;

import static androidx.camera.core.CameraX.getContext;


public class ResetPassword extends Fragment implements View.OnClickListener {
    Context context;
    EditText email;
    Button resetButton;
    ProgressDialog progressDialog;
    UserMngInterface userMngInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email = view.findViewById(R.id.email_reset_password);
        resetButton = view.findViewById(R.id.request_button);
        resetButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading, Please wait");
        progressDialog.setCancelable(false);
        userMngInterface = (UserManagement) context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        String email = this.email.getText().toString().trim();
        reset(email);

    }

    void reset(String email) {
        if (!email.isEmpty()) {
            progressDialog.show();
            Helper.customToast(getActivity(), "Email sent!");
        } else {
            Helper.customToast(getActivity(), "Please Enter email address");
        }

    }
}
