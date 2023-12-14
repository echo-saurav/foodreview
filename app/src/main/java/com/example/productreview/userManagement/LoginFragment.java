package com.example.productreview.userManagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment implements View.OnClickListener {

    EditText email;
    EditText password;
    Button loginButton;
    ProgressDialog progressDialog;
    UserMngInterface userMngInterface;
    Context context;
    TextView goToSignUp;
    TextView goToResetPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email = view.findViewById(R.id.email_login);
        password = view.findViewById(R.id.pass_login);
        goToSignUp = view.findViewById(R.id.go_sign_up);
        goToSignUp.setOnClickListener(this);
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        goToResetPassword =view.findViewById(R.id.go_to_reset_password);
        goToResetPassword.setOnClickListener(this);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading, Please wait");
        progressDialog.setCancelable(false);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userMngInterface = (UserManagement) context;
        this.context = context;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                check();
                break;
            case R.id.go_sign_up:
                userMngInterface.selectSignUp();
                break;
            case R.id.go_to_reset_password:
                userMngInterface.selectResetPassword();
                break;
        }
    }

    void check() {
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            login(email, password);
        } else {
            Helper.customToast(getActivity(),"Please enter email and password");
        }
    }

    void login(String email, String password) {
        progressDialog.show();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    afterLogin();
                }else {
                    progressDialog.dismiss();
                    Helper.customToast(getActivity(),task.getException().getMessage());
                }
            }
        });

    }

    void afterLogin() {
        progressDialog.dismiss();
        getActivity().finish();
        startActivity(new Intent(context, MainActivity.class));
    }
}
