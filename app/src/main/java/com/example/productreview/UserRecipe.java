package com.example.productreview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.adapter.Adapter;
import com.example.productreview.profile.AdapterProfile;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import static com.example.productreview.MainActivity.TAG;

public class UserRecipe extends Fragment implements OnBottomReachedListener{
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ArrayList<Object> arrayList;
    private int limit = 9;
    private Query query;
    private String userUid;
    private FragmentManagement fragmentManagement;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.user_recipe,container,false);
        view = inflater.inflate(R.layout.user_recipe, container, false);
        if (userUid == null) {
            userUid = FirebaseAuth.getInstance().getUid();
            query = FirebaseFirestore.getInstance().collection("recipe").whereEqualTo("userUid", userUid);
        }
        query = FirebaseFirestore.getInstance().collection("recipe").whereEqualTo("userUid", userUid);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayList = new ArrayList<>();
        adapter = new Adapter(arrayList,Adapter.RECIPE_VIEW_TYPE,getContext());
        adapter.setOnBottomReachedListener(this);
        recyclerView = view.findViewById(R.id.recipe_ryc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getAllData(null);
    }

    @Override
    public void onBottomReached(Timestamp timestamp) {
        getAllData(timestamp);
//        Log.d("new test", "onBottomReached: ");
    }

    void getAllData(Timestamp timestamp) {
        Log.d("new test", "getAllData: ");
        if (timestamp == null) {
            Query q = this.query.orderBy("timeStamp", Query.Direction.DESCENDING).limit(limit);
            startQuery(q);
            Log.d("new test", "getAllData: "+"null");
        }
        Query q = this.query.orderBy("timeStamp", Query.Direction.DESCENDING).startAfter(timestamp).limit(limit);
        startQuery(q);
        Log.d("new test", "getAllData: "+"not null");
    }
    void startQuery(Query query) {
        Log.d("new test", "start query: ");
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d(TAG, "onSuccess: is success");
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (documentSnapshot.getType()) {
                        case ADDED:
                            arrayList.add(documentSnapshot.getDocument());
                            adapter.notifyDataSetChanged();
                            Log.d("new test", "getAllData: "+"adding");
                            break;
                    }
                }
            }
        });
    }


    public UserRecipe(String userUid){
        this.userUid=userUid;
    }

}
