package com.example.productreview.search;

import android.content.Context;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.MainActivity;
import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.adapter.Adapter;
import com.example.productreview.adapter.CustomHashMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.productreview.MainActivity.TAG;

public class SearchFragment extends Fragment implements OnBottomReachedListener {
    Context context;
    RecyclerView recyclerView;
    Adapter adapter;
//    CustomHashMap<String ,Object> customHashMap;
    ArrayList<Object> arrayList;
    EditText searchEditText;
    int limit=20;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayList=new ArrayList<>();
//        customHashMap=new CustomHashMap<>();
//        adapter=new Adapter(customHashMap,Adapter.SEARCH_VIEW_TYPE,context);
        adapter=new Adapter(arrayList,Adapter.SEARCH_VIEW_TYPE,context);
        adapter.setOnBottomReachedListener(this);
        recyclerView=view.findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        searchEditText=view.findViewById(R.id.search_query);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                search(searchEditText.getText().toString().toLowerCase());
            }
        });
    }

//    void search(String queryText){
//        arrayList.clear();
//
//        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference();
//        myRef=myRef.child("searchTerms");
//
////        //FireBase search
////        Query name=myRef.orderByChild("tag").limitToFirst(10).startAt(queryText).endAt(queryText+"\uf8ff");
////
////        name.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                for (DataSnapshot child:dataSnapshot.getChildren()){
//////                    customHashMap.add(child.child("id").getValue(String.class),child.getValue());
////                    arrayList.add(dataSnapshot);
////                }
////                adapter.notifyDataSetChanged();
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) { }
////        });
//    }

    void search(String queryText){
        arrayList.clear();
        Query searchUsers= FirebaseFirestore.getInstance().collection("users")
                .whereGreaterThanOrEqualTo("name",queryText);
        //
        Query searchProductDescription= FirebaseFirestore.getInstance().collection("posts")
                .whereGreaterThanOrEqualTo("searchProductDescription",queryText)
                .limit(limit);
        Query searchProductName= FirebaseFirestore.getInstance().collection("posts")
                .whereGreaterThanOrEqualTo("searchProductName",queryText)
                .limit(limit);
        Query searchCompany= FirebaseFirestore.getInstance().collection("posts")
                .whereGreaterThanOrEqualTo("searchCompany",queryText)
                .limit(limit);
        //
        Query searchRecipeName= FirebaseFirestore.getInstance().collection("recipe")
                .whereGreaterThanOrEqualTo("searchRecipeName",queryText)
                .limit(limit);

        myQuery(searchProductName);
        myQuery(searchUsers);
        myQuery(searchCompany);
        myQuery(searchRecipeName);
        myQuery(searchProductDescription);

    }

    void myQuery(Query query) {
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d(TAG, "onSuccess: is success");

            if(queryDocumentSnapshots==null || queryDocumentSnapshots.getDocumentChanges().size()==0){
                Log.d(TAG, "onSuccess: "+"is null");
            }
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (documentSnapshot.getType()) {
                        case ADDED:
                            arrayList.add(documentSnapshot.getDocument());
                            Log.d(TAG, "onSuccess: value:"+documentSnapshot.getDocument().getData().toString());

                            break;

                    }
                }
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }



    @Override
    public void onBottomReached(Timestamp timestamp) {

    }
}
