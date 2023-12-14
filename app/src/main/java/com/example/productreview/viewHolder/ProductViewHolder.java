package com.example.productreview.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FullScreenImageViewer;
import com.example.productreview.Helper;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.FragmentManagement;
import com.example.productreview.adapter.Adapter;
import com.example.productreview.comments.CommentValueHolder;
import com.example.productreview.comments.CommentsFragment;
import com.example.productreview.customDialog.CustomDialog;
import com.example.productreview.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.productreview.MainActivity.TAG;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    private TextView likeCountTextView, productDescriptionTextView, productNameTextView, companyNameTextView, addressTextView, timeTextView, userNameTextView, topCommentTextView;
    private ImageView imageView, userProfileImage;
    private ImageView loveButton, commentButton, currentUserProfileImage;
    private Context context;
    private FragmentManagement fragmentManagement;
    private EditText commentEditText;
    private boolean love = false, save = false;
    private LinearLayout profileId;
    private View view;

    public ProductViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.view=itemView;
        profileId=itemView.findViewById(R.id.profile_id);
        fragmentManagement = (MainActivity) context;
        currentUserProfileImage = itemView.findViewById(R.id.profile_picture);
        userProfileImage = itemView.findViewById(R.id.user_image);
        productNameTextView = itemView.findViewById(R.id.products_name);
        productDescriptionTextView = itemView.findViewById(R.id.products_des);
        companyNameTextView = itemView.findViewById(R.id.company_name);
        addressTextView = itemView.findViewById(R.id.location);
        userNameTextView = itemView.findViewById(R.id.userName);
        imageView = itemView.findViewById(R.id.imageView);
        loveButton = itemView.findViewById(R.id.love_image);
//        saveButton = itemView.findViewById(R.id.save_image);
        commentButton = itemView.findViewById(R.id.comment_image);
        commentEditText = itemView.findViewById(R.id.new_comment);
        topCommentTextView = itemView.findViewById(R.id.top_comment);
        timeTextView = itemView.findViewById(R.id.time);
        likeCountTextView = itemView.findViewById(R.id.likes_count);

    }

    public void setView(Object object,Adapter adapter) {
        final DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
        final String productName = (String) documentSnapshot.getData().get("productName");
        final String productDescription = (String) documentSnapshot.getData().get("productDescription");
        final String address = (String) documentSnapshot.getData().get("address");
        final String userUid = (String) documentSnapshot.getData().get("userUid");
        final GeoPoint geoPoint = (GeoPoint) documentSnapshot.getData().get("location");
        String company = (String) documentSnapshot.getData().get("company");
        String imageUrl = (String) documentSnapshot.getData().get("imageUrl");
        String topComment = (String) documentSnapshot.getData().get("topComment");
        Date date = documentSnapshot.getTimestamp("timeStamp").toDate();
        String id = documentSnapshot.getId();

        productDescriptionTextView.setText(productDescription);
        productNameTextView.setText(productName);
        companyNameTextView.setText(company);
        addressTextView.setText(address);

        loadTopComment(topComment);
        setCommentEditText(documentSnapshot.getId());
        setAddressTextView(geoPoint, address);
        setTimeTextView(date);
        setUserProfileInfo(userUid);
        setLike(id);
        setLoveButton(id);
        setUserNameTextView(userUid);
        setCommentButton(id);
        setLikeCountTextView(id);

        //load current user image
        setCurrentUserProfileImage(FirebaseAuth.getInstance().getUid());
        //load image
        loadImage(imageUrl, imageView);

        imageView.setOnClickListener(view -> {
            Intent fullScreenImageView=new Intent(context, FullScreenImageViewer.class);
            fullScreenImageView.putExtra("imageUrl",imageUrl);
            context.startActivity(fullScreenImageView);
        });


        if(userUid.equals(FirebaseAuth.getInstance().getUid()))
        this.view.findViewById(R.id.more_option).setOnClickListener(view -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(context, this.view.findViewById(R.id.more_option));
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.popup_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(item -> {
                Log.d(TAG, "onMenuItemClick: clicked");
                if(item.getItemId()==R.id.delete){
                    CustomDialog customDialog=new CustomDialog(context,
                            "Do you want to delete this post?",
                            "Click Ok to delete this post",
                            new CustomDialog.customDialogOnClickListener() {
                                @Override
                                public void onYesClick() {
                                    DocumentReference documentReference=
                                            FirebaseFirestore.getInstance()
                                            .collection("posts")
                                            .document(id);

                                    documentReference.delete().addOnCompleteListener(task -> {
                                               adapter.onDelete(object);
                                    });
                                }

                                @Override
                                public void onNoClick() {

                                }
                            }
                    );
                    customDialog.show();
                }

                return true;
            });

            popup.show(); //showing popup menu
        });
        else  this.view.findViewById(R.id.more_option).setVisibility(View.GONE);
    }

    void setCurrentUserProfileImage(String uid) {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("users").document(uid);
        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                String imageUrl = (String) document.get("imageUrl");
                loadImage(imageUrl, currentUserProfileImage);
            }
        });
    }

    void setLikeCountTextView(String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("postsMeta/" + id + "/likesCount/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    likeCountTextView.setVisibility(View.GONE);
                } else {
                    int count = dataSnapshot.getValue(Integer.class);
                    String countText = count + " likes";
                    likeCountTextView.setText(countText);
                    likeCountTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void setCommentButton(final String id) {
        commentButton.setOnClickListener(view -> {
            Fragment fragment;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("postsMeta/" + id + "/comments/");
            fragment = CommentsFragment.newInstance(databaseReference);
            fragmentManagement.showFragment(fragment);
        });
    }

    void setUserNameTextView(final String userUid) {
        this.profileId.setOnClickListener(view -> {
            Fragment fragment;
            if (userUid.equals(FirebaseAuth.getInstance().getUid())) {

                fragment = ProfileFragment.newInstance(fragmentManagement);
                Log.d("Products Click", "onClick: own profile");
                fragmentManagement.showFragment(fragment,imageView,"userImage");
            } else {
                Query query=FirebaseFirestore.getInstance().collection("posts").whereEqualTo("userUid",userUid);
                Log.d("Products Click", "onClick: others profile");

                fragment = ProfileFragment.newInstance(userUid,fragmentManagement);
                fragmentManagement.showFragment(fragment,imageView,"userImage");
            }
        });

    }

    void setLoveButton(final String id) {
        loveButton.setOnClickListener(view -> {
            love(id);
            Log.d("LOVE", "onClick: clicking");
        });
    }

    void setTimeTextView(Date date) {
        //calculating date and timeTextView
        try {
            if (date != null) {
                DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                String creationDate = dateFormat.format(date);
                timeTextView.setText(creationDate);
            }
        } catch (Exception e) {
        }
    }

    void setAddressTextView(final GeoPoint geoPoint, final String address) {
        addressTextView.setOnClickListener(view -> {
            double latitude = geoPoint.getLatitude();
            double longitude = geoPoint.getLongitude();
            String uriBegin = "geo:" + latitude + "," + longitude;
            String query = latitude + "," + longitude + "(" + address + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        });
    }

    void setCommentEditText(final String id) {
        commentEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addNewComment(commentEditText.getText().toString(), id);
                    commentEditText.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    void loadTopComment(String topComment) {
        if (topComment == null) {
            topCommentTextView.setVisibility(View.GONE);
        } else {
            topCommentTextView.setVisibility(View.VISIBLE);
            topCommentTextView.setText(topComment);
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
                    userNameTextView.setText((String) document.get("name"));
                    String imageUrl = (String) document.get("imageUrl");
                    loadImage(imageUrl, userProfileImage);
                }
            }
        });
    }

    void setLike(String id) {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("postsMeta").child(id).child("likes").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    loveButton.setImageResource(R.drawable.love_off);
                    love = false;
                } else {
                    loveButton.setImageResource(R.drawable.love_on_icon);
                    love = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    void love(String id) {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference = databaseReference.child("postsMeta").child(id).child("likes").child(uid);
        if (love) {
            Log.d("Inside function", "love is true");
            databaseReference.removeValue();
        } else {
            Log.d("Inside function", "love is false");
            databaseReference.setValue(true);
        }
    }

    void addNewComment(String text, String postId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("postsMeta/" + postId + "/comments/");
        databaseReference.push().setValue(new CommentValueHolder(user.getDisplayName(), text, user.getUid()));

        Log.d("Comment", "addNewComment " + text);

        DocumentReference documentReference = FirebaseFirestore.getInstance().document("posts/" + postId);
        documentReference.update("topComment", text);
    }

    void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url)
                .error(R.drawable.profile_icon)
                .fit()
                .centerCrop()
                .into(imageView);
    }

}
