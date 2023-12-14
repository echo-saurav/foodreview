package com.example.productreview.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.adapter.Adapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import static com.example.productreview.MainActivity.TAG;

public class Inbox extends Fragment implements OnBottomReachedListener {
    private RecyclerView recyclerView;
    private InboxAdapter adapter;
    private ArrayList<InboxValueHolder> inboxValueHolders;
    private int limit = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.inbox_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inboxValueHolders = new ArrayList<>();
        adapter = new InboxAdapter(inboxValueHolders, getContext(), this);
        recyclerView.setAdapter(adapter);

        String userUid = FirebaseAuth.getInstance().getUid();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users")
                .document(userUid).collection("inbox");
        syncChats(collectionReference);

    }

    void syncChats(CollectionReference collectionReference) {

        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d(TAG, "onSuccess: is success");
            if (queryDocumentSnapshots == null || queryDocumentSnapshots.getDocumentChanges().size() == 0) {
//                adapter.setLoadingVisibility(false);
                Log.d(TAG, "onSuccess: " + "is null");
            }
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (documentSnapshot.getType()) {
                        case ADDED:

                            String userUid = (String) documentSnapshot.getDocument().get("uid");
                            DocumentReference documentReference = FirebaseFirestore.getInstance()
                                    .collection("users").document(userUid);

                            documentReference.get().addOnCompleteListener(task -> {
                                DocumentSnapshot document = task.getResult();

                                InboxValueHolder inboxValueHolder = new InboxValueHolder((String) document.get("imageUrl"),
                                        (String) document.get("name"),
                                        (String) documentSnapshot.getDocument().get("chatId"),
                                        documentSnapshot.getDocument().getId());
                                inboxValueHolders.add(inboxValueHolder);
                                adapter.notifyDataSetChanged();

                            });

                            break;

                    }
                }
            }
        });


//        collectionReference.get()
//                .addOnCompleteListener((value, e) -> {
//                    if (e != null)  return;
//                    inboxValueHolders.clear();
//                    for (QueryDocumentSnapshot doc : value) {
//
//                        String userUid= (String) doc.get("uid");
//                        DocumentReference documentReference = FirebaseFirestore.getInstance()
//                                .collection("users").document(userUid);
//
//                        documentReference.get().addOnCompleteListener(task -> {
//                            DocumentSnapshot document = task.getResult();
//
//                            InboxValueHolder inboxValueHolder=new InboxValueHolder((String) document.get("imageUrl"),
//                                    (String) document.get("name"),
//                                    (String) doc.get("chatId") );
//                            inboxValueHolders.add(inboxValueHolder);
//                            adapter.notifyDataSetChanged();
//
//                        });
//
//                    }
//                    limit=limit+10;
//                });

    }

    public void onBottomReached(Timestamp timestamp) {
//        String userUid = FirebaseAuth.getInstance().getUid();
//        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users")
//                .document(userUid).collection("inbox");
//        limit = limit + 10;
//        syncChats(collectionReference);
    }
}
