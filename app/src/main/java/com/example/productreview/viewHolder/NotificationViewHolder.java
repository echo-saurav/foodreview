package com.example.productreview.viewHolder;

import android.content.Context;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FragmentManagement;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.productDetails.ProductDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import javax.annotation.Nullable;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView heading, description, subtext;
    private Context context;
    private View view;
    private FragmentManagement fragmentManagement;

    public NotificationViewHolder(@NonNull View itemView, Context context) {
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
        DataSnapshot snapshot= (DataSnapshot) object;

        String postId=snapshot.child("postId").getValue(String.class);

        if (snapshot.child("topic").getValue(String.class).equals("like")) {
            String userUid = snapshot.child("userUid").getValue(String.class);
            loadLikeView(userUid,postId);

        } else if(snapshot.child("topic").getValue(String.class).equals("comment")){
            loadCommentView(snapshot.child("commentId").getValue(String.class),postId);
        }
    }

    void loadCommentView(String commentId,String postId){
        loadPost(postId);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("/postsMeta/"+postId+"/comments/"+commentId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName=dataSnapshot.child("userName").getValue(String.class);
                String commentText=dataSnapshot.child("commentText").getValue(String.class);
                heading.setText("new comment by "+userName);
                description.setText(commentText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    void loadLikeView(String userUid, final String postId){
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .document("/users/" +userUid);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String userName=(String) documentSnapshot.get("name");
                heading.setText("your post is liked by "+userName);
                loadPost(postId);
            }
        });
    }

    void loadPost(String postId){
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .document("/posts/" +postId);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String imageUrl=(String) documentSnapshot.getData().get("imageUrl");
                loadImage(imageUrl);
            }
        });

        view.setOnClickListener(view -> {
            ProductDetails productDetails=ProductDetails.newInstance(postId);
            fragmentManagement.showFragment(productDetails);
        });
    }

    void loadImage(String url) {
        Picasso.get().load(url)
                .error(R.drawable.profile_icon)
                .fit()
                .centerCrop()
                .into(imageView);
    }
}
