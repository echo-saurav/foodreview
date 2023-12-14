package com.example.productreview;


import android.view.View;

import androidx.fragment.app.Fragment;

public interface FragmentManagement {
    void showFragment(Fragment fragment);
    void showFragment(Fragment fragment, View view,String name);

}
