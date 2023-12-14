package com.example.productreview.recipeFeed;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.productreview.FragmentManagement;
import com.example.productreview.HeightWrappingViewPager;
import com.example.productreview.R;
import com.example.productreview.addRecipe.RecipeFragment;
import com.example.productreview.profile.ProfileFragment;
import com.example.productreview.roundImage.CircleTransform;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeDetails extends Fragment {
    private String userUid;
    private String recipeName;
    private String userName;
    private String userImageUrl;
    private String time;
    private String imageUrl;
    private static String ingredient;
    private static String preparation;
    private TextView recipeNameText;
    private TextView userNameText;
    private TextView timeText;
    private static TextView ingredientText;
//    private static RichEditor ingredientText;
    private static TextView preparationText;
//    private static RichEditor preparationText;
    private ImageView imageView,userImage;
    static ArrayList<RecipeFragment> fragmentArrayList;
//    static ViewPager viewPager;
    static HeightWrappingViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private FragmentManagement fragmentManagement;
//    private MyTabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe_details,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userNameText=view.findViewById(R.id.userName);
        timeText=view.findViewById(R.id.time_stamp);
//        ingredientText=view.findViewById(R.id.ingredients);
//        preparationText=view.findViewById(R.id.preparation);
        imageView=view.findViewById(R.id.imageView);
        userImage=view.findViewById(R.id.user_image);
        recipeNameText=view.findViewById(R.id.recipe_name);

        fragmentArrayList=new ArrayList<>();
        PreparationFragment preparationFragment = new PreparationFragment();
        IngredientsFragment ingredientsFragment = new IngredientsFragment();

        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(ingredientsFragment);
        fragmentArrayList.add(preparationFragment);
        viewPager = view.findViewById(R.id.recipe_view_pager);
        tabLayout = view.findViewById(R.id.tab);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        userNameText.setText(userName);
        userNameText.setOnClickListener(view1 -> {
            Fragment fragment;
            fragment = ProfileFragment.newInstance(userUid,fragmentManagement);
            fragmentManagement.showFragment(fragment);
        });
        timeText.setText(time);
//        ingredientText.setText(ingredient);
//        preparationText.setText(preparation);
        recipeNameText.setText(recipeName);

        Picasso.get().load(imageUrl).into(imageView);
        loadImage(userImageUrl,userImage);

    }


    void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url)
                .error(R.drawable.profile_icon)
                .placeholder(R.drawable.profile_icon)
                .transform(new CircleTransform())
                .into(imageView);


    }




    public static RecipeDetails newInstance(String recipeName,String userName,String userUid, String userImageUrl, String time, String imageUrl, String ingredient, String preparation,FragmentManagement fragmentManagement) {
        return new RecipeDetails(recipeName,userName,userUid,userImageUrl,time,imageUrl,ingredient,preparation,fragmentManagement);
    }

    public RecipeDetails(String recipeName, String userName, String userUid,String userImageUrl, String time, String imageUrl, String ingredient, String preparation,FragmentManagement fragmentManagement) {
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.time = time;
        this.imageUrl = imageUrl;
        this.ingredient = ingredient;
        this.preparation = preparation;
        this.recipeName=recipeName;
        this.userUid=userUid;
        this.fragmentManagement=fragmentManagement;
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentArrayList.get(position).getTitle();
        }
    }

    public static class IngredientsFragment extends RecipeFragment {
        String title = "Ingredients";


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.ingredient_details, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ingredientText = view.findViewById(R.id.ingredients);
            ingredientText.setText(Html.fromHtml(ingredient));
//            ingredientText.setHtml(ingredient);
//            ingredientText.setInputEnabled(false);


        }

        @Override
        public String getTitle() {
            return this.title;
        }
    }

    public static class PreparationFragment extends RecipeFragment {
        String title = "Preparation";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.preparation_details, container, false);
        }


        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            preparationText = view.findViewById(R.id.preparation);
            preparationText.setText(Html.fromHtml(preparation));
//            preparationText.setHtml(preparation);
//            preparationText.setPadding(10,10,10,20);
//            preparationText.setInputEnabled(false);
        }

        @Override
        public String getTitle() {
            return this.title;
        }
    }


}
