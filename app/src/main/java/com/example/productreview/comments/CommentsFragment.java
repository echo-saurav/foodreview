package com.example.productreview.comments;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.Helper;
import com.example.productreview.R;
import com.example.productreview.adapter.CustomHashMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class CommentsFragment extends Fragment {

    Context context;
    DatabaseReference databaseReference;
    CustomHashMap<String, DataSnapshot> commentValueHolderCustomHashMap;
    RecyclerView recyclerView;
    AdapterComment adapterComment;
    EditText newComment;
    ImageView userImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newComment = view.findViewById(R.id.new_comment);
        userImage=view.findViewById(R.id.user_image);
        loadProfilePicture();
        newComment.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    addNewComment(newComment.getText().toString());
                    newComment.setText("");

                    return true;
                }
                return false;
            }
        });

        commentValueHolderCustomHashMap = new CustomHashMap<>();
        recyclerView = view.findViewById(R.id.comment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapterComment = new AdapterComment(commentValueHolderCustomHashMap,context);
        recyclerView.setAdapter(adapterComment);
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                commentValueHolderCustomHashMap.add(s, dataSnapshot);
                adapterComment.notifyItemInserted(commentValueHolderCustomHashMap.getIndexByKey(s));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                commentValueHolderCustomHashMap.change(s, dataSnapshot);
                adapterComment.notifyItemChanged(commentValueHolderCustomHashMap.getIndexByKey(s));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                commentValueHolderCustomHashMap.remove(dataSnapshot.getKey());
                adapterComment.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void addNewComment(String text) {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.push().setValue(new CommentValueHolder(user.getDisplayName(), text,user.getUid()));

        Log.d("Comment", "addNewComment " + text);
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
                                .into(userImage);
                    } else {
                        Helper.showToast(context, "Error loading profile picture!");
                    }
                }
            });
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    public static CommentsFragment newInstance(DatabaseReference databaseRef) {
        CommentsFragment fragment = new CommentsFragment();
        fragment.setDatabaseReference(databaseRef);
        return fragment;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }
}
