package com.example.productreview.notification;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.adapter.Adapter;
import com.example.productreview.adapter.CustomHashMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    Context context;
    RecyclerView recyclerView;
    Adapter adapter;
//    CustomHashMap<String ,Object> customHashMap;
    ArrayList<Object> arrayList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notification,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        customHashMap=new CustomHashMap<>();
        arrayList=new ArrayList<>();
//        adapter=new Adapter(customHashMap,Adapter.NOTIFICATION_VIEW_TYPE,context);
        adapter=new Adapter(arrayList,Adapter.NOTIFICATION_VIEW_TYPE,context);
        recyclerView=view.findViewById(R.id.notification_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        String uid= FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference=databaseReference.child("userNotification").child(uid);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                customHashMap.add(s, dataSnapshot);
                arrayList.add(dataSnapshot);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).bottomNavigationView.getMenu().findItem(R.id.notification).setChecked(true);
    }
}
