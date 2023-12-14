package com.example.productreview.productDetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.productreview.FragmentManagement;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.productreview.MainActivity.TAG;

public class ProductDetails extends Fragment {
    private TextView productDescription,productName, companyName, location, time, userName,topComment;
    private EditText commentEditText;
    private ImageView imageView, userImage;
    private ImageView loveButton,  commentButton, profilePicture;
    View view;
    boolean isLoadedBefore;
    private String id;
    private boolean love = false, save = false;
    private FragmentManagement fragmentManagement;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!isLoadedBefore) {
            view = inflater.inflate(R.layout.product_view, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(this.view, savedInstanceState);
        profilePicture=view.findViewById(R.id.profile_picture);
        productName = view.findViewById(R.id.products_name);
        productDescription = view.findViewById(R.id.products_des);
        companyName = view.findViewById(R.id.company_name);
        location = view.findViewById(R.id.location);
        userImage = view.findViewById(R.id.user_image);
        userName = view.findViewById(R.id.userName);
        imageView = view.findViewById(R.id.imageView);
        loveButton = view.findViewById(R.id.love_image);
//        saveButton = view.findViewById(R.id.save_image);
        commentButton=view.findViewById(R.id.comment_image);
        commentEditText=view.findViewById(R.id.new_comment);
        topComment=view.findViewById(R.id.top_comment);
        time = view.findViewById(R.id.time);


    }

    @Override
    public void onStart() {
        super.onStart();
        DocumentReference documentReference= FirebaseFirestore.getInstance().document("posts/"+id);
//        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                if (documentSnapshot!=null) setValue(documentSnapshot);
//            }
//        });

        Log.d(MainActivity.TAG, "id: "+id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult()!=null){
                    setValue(task.getResult());
                }
            }
        });
    }

    public void setValue(final DocumentSnapshot documentSnapshot) {
        String company = (String) documentSnapshot.getData().get("company");
        final String productName = (String) documentSnapshot.getData().get("productName");
        final String productDes = (String) documentSnapshot.getData().get("productDescription");
        final String loc = (String) documentSnapshot.getData().get("address");
        String imageUrl = (String) documentSnapshot.getData().get("imageUrl");
        final String userUid = (String) documentSnapshot.getData().get("userUid");
        final GeoPoint geoPoint =(GeoPoint) documentSnapshot.getData().get("location");


        loadProfilePicture();
        String topC=(String)documentSnapshot.getData().get("topComment");
        if(topC==null){
            topComment.setVisibility(View.GONE);
        }else {
            topComment.setVisibility(View.VISIBLE);
            topComment.setText(topC);
        }
        commentEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addNewComment(commentEditText.getText().toString(),documentSnapshot.getId());
                    commentEditText.setText("");
                    return true;
                }
                return false;
            }
        });

        Picasso.get().load(imageUrl)
                .error(R.drawable.add_icon)
                .placeholder(R.mipmap.icon)
                .fit()
                .centerCrop()
                .into(imageView);

        productDescription.setText(productDes);
        this.productName.setText(productName);
        companyName.setText(company);
        location.setText(loc);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = geoPoint.getLatitude();
                double longitude = geoPoint.getLongitude();
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + loc + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });

        //calculating date and time
        try {
            Date date = documentSnapshot.getTimestamp("timeStamp").toDate();

            if (date != null) {

                DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                String creationDate = dateFormat.format(date);
                time.setText(creationDate);
            }
        } catch (Exception e) {
        }

        if (userUid != null) {

            setUserProfileInfo(userUid);
            setLike(documentSnapshot.getId());

            loveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    love(documentSnapshot.getId());
                    Log.d("LOVE", "onClick: clicking");
                }
            });

            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment;
                    if (userUid.equals(FirebaseAuth.getInstance().getUid())) {

                        fragment = ProfileFragment.newInstance(fragmentManagement);
                        Log.d("Products Click", "onClick: own profile");
                        fragmentManagement.showFragment(fragment);
                    } else {
                        fragment = ProfileFragment.newInstance(userUid,fragmentManagement);
                        Log.d("Products Click", "onClick: others profile");
                        fragmentManagement.showFragment(fragment);
                    }

                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment;
                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("postsMeta/"+documentSnapshot.getId()+"/comments/");
                    fragment = CommentsFragment.newInstance(databaseReference);
                    fragmentManagement.showFragment(fragment);
                }
            });
        }

        if(userUid.equals(FirebaseAuth.getInstance().getUid()))
            this.view.findViewById(R.id.more_option).setOnClickListener(view -> {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), this.view.findViewById(R.id.more_option));
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(item -> {
                    Log.d(TAG, "onMenuItemClick: clicked");
                    if(item.getItemId()==R.id.delete){
                        CustomDialog customDialog=new CustomDialog(getContext(),
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
                                            startActivity(new Intent(getContext(),MainActivity.class));
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

                    Picasso.get().load(imageUrl)
                            .error(R.drawable.profile_icon)
                            .fit()
                            .centerCrop()
                            .into(userImage);

                }
            }
        });
    }

    void loadProfilePicture(){
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
                                .fit()
                                .centerCrop()
                                .into(profilePicture);
                    }
                }
            });
        }
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

    void addNewComment(String text,String postId) {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("postsMeta/"+postId+"/comments/");
        databaseReference.push().setValue(new CommentValueHolder(user.getDisplayName(), text,user.getUid()));

        Log.d("Comment", "addNewComment " + text);

        DocumentReference documentReference=FirebaseFirestore.getInstance().document("posts/"+postId);
        documentReference.update("topComment",text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentManagement=(MainActivity)context;
    }


    public static ProductDetails newInstance(String id) {
        ProductDetails fragment = new ProductDetails();
        fragment.setId(id);
        return fragment;
    }

    void setId(String id){
        this.id=id;
    }
}
