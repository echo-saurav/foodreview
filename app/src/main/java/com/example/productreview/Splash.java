package com.example.productreview;

import android.content.Intent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.productreview.userManagement.UserManagement;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.messaging.FirebaseMessaging;


public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.splash);

        //idk anything about this, timestamp causing me error
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        //notificationFragment
        FirebaseMessaging.getInstance().subscribeToTopic("all");

    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadUserInfo();
    }

    void reloadUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        goToHome();

                    } else {
                        Helper.customToast(Splash.this, task.getException().getMessage());
                        goToLogin();
                    }
                }
            });
        else
            goToLogin();
    }


    void goToLogin() {
        startActivity(new Intent(this, UserManagement.class));
        finish();
    }

    void goToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
