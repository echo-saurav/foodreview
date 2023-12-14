package com.example.productreview.feed;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.adapter.Adapter;
import com.example.productreview.adapter.CustomHashMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.productreview.MainActivity.TAG;

public class Feed extends Fragment implements OnBottomReachedListener {
    private Context context;
    private RecyclerView feedRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
//    private CustomHashMap<String, Object> customHashMap;
    private ArrayList<Object> arrayList;
    private Adapter adapter;
    private View view;
    boolean isLoadedBefore;
    private Query query;
    private int VIEW_TYPE;
    private boolean scrolling = false;
    private DocumentSnapshot documentSnapshot;
    private int limit=2;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: feed");
        if (!isLoadedBefore) {
            view = inflater.inflate(R.layout.feed, container, false);
//            customHashMap = new CustomHashMap<>();
            arrayList=new ArrayList<>();
//            adapter = new Adapter(customHashMap, VIEW_TYPE, context);
            adapter=new Adapter(arrayList,VIEW_TYPE,context);
            adapter.setOnBottomReachedListener(this);
            adapter.setLoadingVisibility(true);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: feed");
        if (!isLoadedBefore) {

            feedRecyclerView = view.findViewById(R.id.feed_recycler_view);
            feedRecyclerView.setNestedScrollingEnabled(true);
            feedRecyclerView.setLayoutManager(layoutManager);
            feedRecyclerView.setAdapter(adapter);
            adapter.setLoadingVisibility(true);
            getAllData(null);

        }
        isLoadedBefore = true;
    }

    @Override
    public void onBottomReached(final Timestamp timestamp) {
        adapter.setLoadingVisibility(true);
        getAllData(timestamp);
        Log.d(TAG, "onBottomReached start id after:"+timestamp);

    }

    void getAllData(Timestamp timestamp) {

        if(timestamp==null){
//            Query q=FirebaseFirestore.getInstance().collection("posts").orderBy("timeStamp",Query.Direction.DESCENDING).limit(limit);
//            myQuery(q);

            Query q= this.query.orderBy("timeStamp",Query.Direction.DESCENDING).limit(limit);
            myQuery(q);
        }
//        Query q=FirebaseFirestore.getInstance().collection("posts").orderBy("timeStamp",Query.Direction.DESCENDING).startAfter(timestamp).limit(limit);
//        myQuery(q);

        Query q= this.query.orderBy("timeStamp",Query.Direction.DESCENDING).startAfter(timestamp).limit(limit);
        myQuery(q);


    }

    void myQuery(Query query) {
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d(TAG, "onSuccess: is success");
            if(queryDocumentSnapshots==null || queryDocumentSnapshots.getDocumentChanges().size()==0){
                adapter.setLoadingVisibility(false);
                Log.d(TAG, "onSuccess: "+"is null");
            }
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

    public static Feed newInstance(RecyclerView.LayoutManager layoutManager, Query query, int VIEW_TYPE) {
        Feed fragment = new Feed();
        fragment.setFragment(layoutManager, query, VIEW_TYPE);

        return fragment;
    }

    void setFragment(RecyclerView.LayoutManager layoutManager, Query query, int VIEW_TYPE) {
        this.layoutManager = layoutManager;
        this.query = query;
        this.VIEW_TYPE = VIEW_TYPE;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: feed");
        isLoadedBefore = false;

    }

}
