package com.example.productreview.home;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FragmentManagement;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.adapter.Adapter;
import com.example.productreview.adapter.CustomHashMap;
import com.example.productreview.feed.Feed;
import com.example.productreview.messages.Inbox;
import com.example.productreview.search.SearchFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.productreview.MainActivity.TAG;

public class HomeFragment extends Fragment {

    private Context context;
    private Feed feed;
    boolean isLoadedBefore;
    private View view;
    public static CustomHashMap<String, Object> sponsorAdsHashMap;
    static int currentAdPosition=0;
    private FragmentManagement fragmentManagement;
    TextView searchText;


    private RecyclerView recyclerView;
    private ArrayList<Object> arrayList;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        fragmentManagement=(MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: home");
        if (!isLoadedBefore) {
            view = inflater.inflate(R.layout.home, container, false);
            sponsorAdsHashMap = new CustomHashMap<>();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: home");
        if (!isLoadedBefore) {
            searchText=view.findViewById(R.id.search);
            searchText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentManagement.showFragment(new SearchFragment(),view,"search");
                }
            });

            Query query = FirebaseFirestore.getInstance().collection("posts");
            feed = Feed.newInstance(new LinearLayoutManager(getContext()), query, Adapter.FEED_VIEW_TYPE);

            FragmentTransaction fragmentTransaction;
            fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.home_feed, feed, "feed");
            fragmentTransaction.commit();

            getAllSponsorAds();

            view.findViewById(R.id.message).setOnClickListener(view1 -> fragmentManagement.showFragment(new Inbox()));
        }

        isLoadedBefore = true;

    }

    void getAllSponsorAds() {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("sponsorAds");

        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (documentSnapshot.getType()) {
                        case ADDED:
                            documentSnapshot.getDocument().getId();
                            sponsorAdsHashMap.add(documentSnapshot.getDocument().getId(), documentSnapshot.getDocument());
                            break;

                        case REMOVED:
                            sponsorAdsHashMap.remove(documentSnapshot.getDocument().getId());
                            break;

                        case MODIFIED:
                            sponsorAdsHashMap.change(documentSnapshot.getDocument().getId(), documentSnapshot.getDocument());
                            break;
                    }
                }
            }

        });
    }

    public static Object getObject() {
        Object object;
        if(currentAdPosition>=sponsorAdsHashMap.size()){
            currentAdPosition=0;
            return sponsorAdsHashMap.getValueByIndex(currentAdPosition);
        }
        object=sponsorAdsHashMap.getValueByIndex(currentAdPosition);
        currentAdPosition++;
        return object;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
//        MainActivity.getInstance().bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isLoadedBefore = false;
        Log.d(TAG, "onDestroy: home");
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

}
