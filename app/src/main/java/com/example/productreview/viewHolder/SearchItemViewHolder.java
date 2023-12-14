package com.example.productreview.viewHolder;

import android.content.Context;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FragmentManagement;
import com.example.productreview.Helper;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.productDetails.ProductDetails;
import com.example.productreview.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.Map;

import javax.annotation.Nullable;

public class SearchItemViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView heading, description, subtext;
    private Context context;
    private View view;
    private FragmentManagement fragmentManagement;


    public SearchItemViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        heading = itemView.findViewById(R.id.heading);
        description = itemView.findViewById(R.id.description);
        subtext = itemView.findViewById(R.id.subtext);
        this.context = context;
        fragmentManagement=(MainActivity)context;
        this.view=itemView;
    }

    public void setView(Object object) {
        final DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
//        final String productName = (String) documentSnapshot.getData().get("productName");
        Map<String ,Object> data=documentSnapshot.getData();

        if(data.containsKey("productName")){
            loadProduct(documentSnapshot.getId());
        }
        if(data.containsKey("email")){
            loadUserProfile(documentSnapshot.getId());
        }

        if(data.containsKey("recipeName")){
            loadRecipe(documentSnapshot.getId());
        }
//        HashMap<String, String> searchValueHolder = (HashMap<String, String>) object;
//        String id = searchValueHolder.get("id");
//
//        if (searchValueHolder.get("category").equals("userName")
//                || searchValueHolder.get("category").equals("userEmail")) {
//            loadUserProfile(id);
//        } else {
//            loadProduct(id);
//        }
    }

    void loadRecipe(String id){
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .document("/recipe/" + id);
        documentReference.get().addOnCompleteListener(task -> {
            String imageUrl = (String) task.getResult().getData().get("imageUrl");
            String recipeName = (String) task.getResult().getData().get("recipeName");

            heading.setText(recipeName);
            Picasso.get().load(imageUrl)
                    .error(R.drawable.profile_icon)
                    .placeholder(R.drawable.profile_icon)
                    .fit()
                    .centerCrop()
                    .into(imageView);


            view.setOnClickListener(view -> {
                ProductDetails productDetails=ProductDetails.newInstance(id);
                fragmentManagement.showFragment(productDetails);
            });
        });

    }

    void loadUserProfile(final String uid) {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .document("/users/" + uid);
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            String imageUrl = (String) documentSnapshot.getData().get("imageUrl");
            String userName = (String) documentSnapshot.getData().get("name");
            String email = (String) documentSnapshot.getData().get("email");
            String bio = (String) documentSnapshot.getData().get("bio");

            description.setText(bio);
            heading.setText(userName);
            subtext.setText(email);
            Picasso.get().load(imageUrl)
                    .error(R.drawable.profile_icon)
                    .placeholder(R.drawable.profile_icon)
                    .fit()
                    .centerCrop()
                    .into(imageView);

        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment profileFragment= ProfileFragment.newInstance(uid,fragmentManagement);
                fragmentManagement.showFragment(profileFragment);
            }
        });
    }


    void loadProduct(final String id) {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .document("/posts/" + id);
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            try {
                String imageUrl = (String) documentSnapshot.getData().get("imageUrl");
                String productDescription = (String) documentSnapshot.getData().get("productDescription");
                String productName = (String) documentSnapshot.getData().get("productName");
                String company = (String) documentSnapshot.getData().get("company");

                description.setText(productName);
                heading.setText(productDescription);
                subtext.setText(company);
                Picasso.get().load(imageUrl)
                        .error(R.drawable.profile_icon)
                        .placeholder(R.drawable.profile_icon)
                        .fit()
                        .centerCrop()
                        .into(imageView);


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProductDetails productDetails=ProductDetails.newInstance(id);
                        fragmentManagement.showFragment(productDetails);
                    }
                });
            }catch (Exception exception){
                setVisibility(false);
            }

        });

    }

    public void setVisibility(boolean isVisible){
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)view.getLayoutParams();
        if (isVisible){
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        }else{
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }

}
