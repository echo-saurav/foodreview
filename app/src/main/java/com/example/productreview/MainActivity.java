package com.example.productreview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.productreview.home.HomeFragment;
import com.example.productreview.notification.NotificationFragment;
import com.example.productreview.profile.ProfileFragment;
import com.example.productreview.recipeFeed.RecipeFeed;
import com.example.productreview.roundImage.CircleTransform;
import com.example.productreview.search.SearchFragment;
import com.example.productreview.upload.UploadActivity;
import com.example.productreview.userManagement.UserManagement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        FirebaseAuth.AuthStateListener ,
        FragmentManagement{
    static MainActivity mainActivity;

    //
    Target target;
    public BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    ProfileFragment profileFragment;
    RecipeFeed recipeFeed;
    NotificationFragment notificationFragment;


    public static String TAG="MY_LOG";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        mainActivity=this;

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //fragments
        profileFragment = ProfileFragment.newInstance(this);
        recipeFeed=RecipeFeed.newInstance();
        notificationFragment = new NotificationFragment();
        homeFragment = HomeFragment.newInstance();

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.parent_frame, homeFragment, "homeFragment");
        fragmentTransaction.commit();

        FirebaseAuth.getInstance().addAuthStateListener(this);

        loadProfilePicture();
//

    }

    void changeFragment(Fragment fragment) {
        replaceFragment(fragment);
//        if (fragment.isAdded()) {
//            return;
//        }
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//        FragmentOne frStack = getSupportFragmentManager().findFragmentByTag(tag);
//
//        if (frStack == null) {
//            fragmentTransaction.replace(R.id.parent_frame, fragment, tag);
//        } else {
//            fragmentTransaction.replace(R.id.parent_frame, frStack);
//        }
//
//        fragmentTransaction.addToBackStack(tag);
//        fragmentTransaction.commit();
    }

    private void replaceFragment (Fragment fragment){

        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.parent_frame, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void replaceFragment(Fragment fragment,View view,String name){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
//            ft.addSharedElement(view,name);
            ft.replace(R.id.parent_frame, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                changeFragment(homeFragment);
                return true;
            case R.id.profile:
                changeFragment(profileFragment);
                return true;
            case R.id.notification:
                changeFragment(notificationFragment);
                return true;
            case R.id.recipe:
                changeFragment(recipeFeed);
                return true;
            case R.id.upload:
                startActivity(new Intent(this, UploadActivity.class));
                return true;
        }
        return false;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.d(TAG, "onAuthStateChanged");
        if (firebaseAuth.getCurrentUser() == null) {
            Log.d(TAG,"user null");
            startActivity(new Intent(this, UserManagement.class));
            finish();
        }
    }

    @Override
    public void showFragment(Fragment fragment) {
        changeFragment(fragment);
    }

    @Override
    public void showFragment(Fragment fragment, View view, String name) {
        replaceFragment(fragment,view,name);
    }

    void loadProfilePicture(){
        target=new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                bottomNavigationView.getMenu().findItem(R.id.profile).setIcon(mBitmapDrawable);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection("users").document(user.getUid());

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String imageUrl = (String) document.get("imageUrl");
                        Picasso.get().load(imageUrl)
                                .error(R.drawable.profile_icon)
                                .placeholder(R.drawable.profile_icon)
                                .transform(new CircleTransform())
                                .into(target);

                    }
                }
            });
        }

    }

    public static MainActivity getInstance() {
        if(mainActivity==null){
            return new MainActivity();
        }
        return mainActivity;
    }


}
