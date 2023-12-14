package com.example.productreview.viewHolder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FragmentManagement;
import com.example.productreview.FullScreenImageViewer;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.recipeFeed.RecipeDetails;
import com.example.productreview.roundImage.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.productreview.MainActivity.TAG;

public class RecipeViewHolder extends RecyclerView.ViewHolder {
    private TextView userName, timeStamp, recipeName;
    private ImageView userProfileImage, recipeImage;
    private View view;
    private FragmentManagement fragmentManagement;
    private String creationTime;
    String recipeNameString;
    String ingredients;
    String preparation;
    String recipeImageUrl;

    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.userName);
        timeStamp = itemView.findViewById(R.id.time_stamp);
        recipeName = itemView.findViewById(R.id.recipe_name);
        userProfileImage = itemView.findViewById(R.id.user_image);
        recipeImage = itemView.findViewById(R.id.imageView);

        this.view = itemView;
        fragmentManagement = MainActivity.getInstance();

    }

    public void setView(Object object) {
        final DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
        recipeNameString = (String) documentSnapshot.getData().get("recipeName");
        ingredients = (String) documentSnapshot.getData().get("ingredients");
        final String address = (String) documentSnapshot.getData().get("address");
        final String userUid = (String) documentSnapshot.getData().get("userUid");
        final GeoPoint geoPoint = (GeoPoint) documentSnapshot.getData().get("location");
        preparation = (String) documentSnapshot.getData().get("preparation");
        recipeImageUrl = (String) documentSnapshot.getData().get("imageUrl");
        String topComment = (String) documentSnapshot.getData().get("topComment");
        Date date = documentSnapshot.getTimestamp("timeStamp").toDate();
        String id = documentSnapshot.getId();
        //
        Picasso.get().load(recipeImageUrl).into(recipeImage);

        setTimeTextView(date);
        setUserProfileInfo(userUid);
        this.recipeName.setText(recipeNameString);


    }

    void setTimeTextView(Date date) {
        //calculating date and timeTextView
        try {
            if (date != null) {
                DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                creationTime = dateFormat.format(date);
                timeStamp.setText(creationTime);
            }
        } catch (Exception e) {
        }
    }

    void setUserProfileInfo(String uid) {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("users").document(uid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userName.setText((String) document.get("name"));
                    String imageUrl = (String) document.get("imageUrl");
                    String userUid = document.getId();
                    loadImage(imageUrl, userProfileImage);
                    Log.d("Test", "onComplete: userUid"+userUid);

                    view.setOnClickListener(view ->
                            fragmentManagement.showFragment(RecipeDetails.newInstance(recipeNameString,
                                    (String) document.get("name"),
                                    userUid,
                                    imageUrl,
                                    creationTime,
                                    recipeImageUrl,
                                    ingredients,
                                    preparation,
                                    fragmentManagement
                                    )
                            )
                    );
                }
            }
        });
    }

    void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url)
                .error(R.drawable.profile_icon)
                .placeholder(R.drawable.profile_icon)
                .transform(new CircleTransform())
                .into(imageView);
    }


}
