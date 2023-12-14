package com.example.productreview.userManagement;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.google.firebase.auth.FirebaseAuth;

public class UserManagement extends AppCompatActivity implements UserMngInterface , FirebaseAuth.AuthStateListener {
    FrameLayout frameLayout;
    LoginFragment loginFragment;
    SignUpFragment signUpFragment;
    ResetPassword resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.user_management);

        frameLayout = findViewById(R.id.userMngFrame);
        loginFragment = new LoginFragment();
        signUpFragment = new SignUpFragment();
        resetPassword = new ResetPassword();

        getSupportFragmentManager().beginTransaction().replace(R.id.userMngFrame, loginFragment).commit();

    }

    @Override
    public void selectLogin() {
        changeFragment(loginFragment,"login");
    }

    @Override
    public void selectSignUp() {
        changeFragment(signUpFragment,"signUp");
    }

    @Override
    public void selectResetPassword() {
        changeFragment(resetPassword,"resetPassword");
    }
    void changeFragment(Fragment fragment, String tag) {
        if (fragment.isAdded()) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment frStack = getSupportFragmentManager().findFragmentByTag(tag);

        if (frStack == null) {
            fragmentTransaction.replace(R.id.userMngFrame, fragment, tag);
        } else {
            fragmentTransaction.replace(R.id.userMngFrame, frStack);
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(UserManagement.this, MainActivity.class));
            finish();
        }
    }
}
