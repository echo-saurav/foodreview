package com.example.productreview.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FragmentManagement;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.productDetails.ProductDetails;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ProfileProductViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private View view;
    private FragmentManagement fragmentManagement;

    public ProfileProductViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        this.view = itemView;
        fragmentManagement = (MainActivity) context;
    }

    public void setView(Object object) {
        DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;

        String imageUrl = (String) documentSnapshot.getData().get("imageUrl");
        final String id = documentSnapshot.getId();

        Picasso.get().load(imageUrl)
                .error(R.drawable.add_icon)
                .placeholder(R.mipmap.icon)
                .fit()
                .centerCrop()
                .into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductDetails productDetails = ProductDetails.newInstance(id);
                fragmentManagement.showFragment(productDetails);
            }
        });


    }
}
