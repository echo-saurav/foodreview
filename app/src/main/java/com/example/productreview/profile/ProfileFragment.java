package com.example.productreview.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FragmentManagement;
import com.example.productreview.Helper;
import com.example.productreview.MainActivity;
import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.UserRecipe;
import com.example.productreview.customDialog.CustomDialog;
import com.example.productreview.editProfile.EditProfile;
import com.example.productreview.messages.Chat;
import com.example.productreview.roundImage.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.productreview.MainActivity.TAG;

public class ProfileFragment extends Fragment implements OnBottomReachedListener, Toolbar.OnMenuItemClickListener {

    private TextView profileNameTextView, bioTextView, toolbarText;
    private String name, bio;
    private Toolbar toolbar;
    private ImageView userImage;
    private String imageUrl;
    private View view;
    private Button followingButton;
    private LinearLayout otherProfileProperties;
    //
    private RecyclerView recyclerView;
    private AdapterProfile adapter;
    private ArrayList<Object> arrayList;
    private int limit = 9;
    private Query query;
    private String userUid;
    private FragmentManagement fragmentManagement;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile, container, false);
        if (userUid == null) {
            userUid = FirebaseAuth.getInstance().getUid();
            query = FirebaseFirestore.getInstance().collection("posts").whereEqualTo("userUid", userUid);
        }
        query = FirebaseFirestore.getInstance().collection("posts").whereEqualTo("userUid", userUid);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        followingButton=view.findViewById(R.id.follow_button);
        otherProfileProperties = view.findViewById(R.id.other_profile_properties);
        toolbarText = view.findViewById(R.id.toolbarText);
        profileNameTextView = view.findViewById(R.id.name);
        bioTextView = view.findViewById(R.id.bio);
        userImage = view.findViewById(R.id.imageView);
        toolbar = view.findViewById(R.id.profile_toolbar);
        toolbar.inflateMenu(R.menu.profile_menu);
        toolbar.setOnMenuItemClickListener(this);
        //
        arrayList = new ArrayList<>();
        adapter = new AdapterProfile(arrayList, getContext());
        adapter.setOnBottomReachedListener(this);
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
        //
//            if (query == null) {
//                query = firebaseFirestore.collection("posts").whereEqualTo("userUid", FirebaseAuth.getInstance().getUid());
//                Log.d(TAG, "onViewCreated: this is user profile");
//
//            }else {
//                Log.d(TAG, "onViewCreated: others profile");
//            }
        getAllData(null);
        start();

        if (userUid.equals(FirebaseAuth.getInstance().getUid())) {
            otherProfileProperties.setVisibility(View.GONE);
        } else {
            otherProfileProperties.setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.message_button).setOnClickListener(view1 -> {
            fragmentManagement.showFragment(new Chat(userUid));
        });


        view.findViewById(R.id.follow_button).setOnClickListener(view12 -> {
            String currentUserUid=FirebaseAuth.getInstance().getUid();
            CollectionReference collectionReference =FirebaseFirestore.getInstance().collection("users")
                    .document(currentUserUid).collection("following");

            HashMap<String,Object> data=new HashMap<>();
            data.put("timeStamp",FieldValue.serverTimestamp());
            data.put("userUid",userUid);
            collectionReference.document(userUid).set(data);

        });

        view.findViewById(R.id.recipe_button).setOnClickListener(view13 -> {
            UserRecipe userRecipe=new UserRecipe(userUid);
            fragmentManagement.showFragment(userRecipe);
        });
    }

    void startQuery(Query query) {
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d(TAG, "onSuccess: is success");
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (documentSnapshot.getType()) {
                        case ADDED:
                            arrayList.add(documentSnapshot.getDocument());
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
    }

    void checkIfUserFollow(){
        String currentUserUid=FirebaseAuth.getInstance().getUid();
        DocumentReference documentReference=FirebaseFirestore.getInstance().collection("users")
                .document(currentUserUid).collection("following").document(userUid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    followingButton.setBackgroundResource(R.drawable.button_ripple);
                    followingButton.setTextColor(Color.WHITE);
                }else {
                    followingButton.setBackgroundResource(R.drawable.edit_text);
                    followingButton.setTextColor(Color.BLACK);
                }
            }
        });
    }

    void saved() {
        Helper.showToast(getContext(), "Show All saved products!");
    }

    void start() {
        if (userUid != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection("users").document(userUid);

            documentReference.get().addOnCompleteListener(task -> {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    name = (String) document.get("name");
                    bio = (String) document.get("bio");
                    imageUrl=(String)document.get("imageUrl");
                    profileNameTextView.setText(name);
                    bioTextView.setText(bio);
                    toolbarText.setText(name);
                    //
                    String imageUrl = (String) document.get("imageUrl");
                    Picasso.get().load(imageUrl)
                            .error(R.drawable.profile_icon)
                            .placeholder(R.drawable.profile_icon)
                            .fit()
                            .centerCrop()
                            .transform(new CircleTransform())
                            .into(userImage);

                } else {
                    Helper.showToast(getContext(), "Error loading profile picture!");
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (userUid.equals(FirebaseAuth.getInstance().getUid())) {
            ((MainActivity) getActivity()).bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
        }
    }



    @Override
    public void onBottomReached(Timestamp timestamp) {
        getAllData(timestamp);
    }

    void getAllData(Timestamp timestamp) {
        if (timestamp == null) {
            Query q = this.query.orderBy("timeStamp", Query.Direction.DESCENDING).limit(limit);
            startQuery(q);
        }
        Query q = this.query.orderBy("timeStamp", Query.Direction.DESCENDING).startAfter(timestamp).limit(limit);
        startQuery(q);

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.logout:
                logout();
                break;
//            case R.id.saved:
//                saved();
//                break;
            case R.id.edit_profile:
                startActivity(new Intent(getContext(), EditProfile.class)
                        .putExtra("name", name)
                        .putExtra("bio", bio)
                        .putExtra("imageUrl", imageUrl)
                );
        }
        return false;
    }

    void logout() {
        CustomDialog customDialog = new CustomDialog(
                getContext(),
                "Logout From Apps",
                "Are you sure ?",
                new CustomDialog.customDialogOnClickListener() {
                    @Override
                    public void onYesClick() {
                        FirebaseAuth.getInstance().signOut();
                        Helper.showToast(getContext(), "Logout!");
                    }

                    @Override
                    public void onNoClick() {
                    }
                }
        );

        customDialog.show();
    }


    public static ProfileFragment newInstance(FragmentManagement fragmentManagement) {
        return new ProfileFragment(fragmentManagement);
    }

    ProfileFragment(FragmentManagement fragmentManagement) {
        this.fragmentManagement=fragmentManagement;
    }

    ProfileFragment(String userUid,FragmentManagement fragmentManagement) {
        this.userUid = userUid;
        this.fragmentManagement=fragmentManagement;
    }

    public static ProfileFragment newInstance(String userUid,FragmentManagement fragmentManagement) {
        return new ProfileFragment(userUid,fragmentManagement);
    }
}
