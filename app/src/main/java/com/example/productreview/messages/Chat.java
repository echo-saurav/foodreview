package com.example.productreview.messages;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.Helper;
import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.adapter.Adapter;
import com.example.productreview.adapter.CustomHashMap;
import com.example.productreview.roundImage.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.productreview.MainActivity.TAG;

public class Chat extends Fragment implements OnBottomReachedListener {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private ArrayList<Object> arrayList;
    private String otherUserUid;
    private String currentUserUid;
    private EditText messageEditText;
    private boolean isInboxExist;
    private DocumentReference inboxRef;
    private String chatId;
    private ImageView imageView;
    private TextView textView;
    private int limit =10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messageEditText = view.findViewById(R.id.message_edit_text);
        recyclerView = view.findViewById(R.id.message_recycler_view);
        imageView=view.findViewById(R.id.imageView);
        textView=view.findViewById(R.id.userName);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.getStackFromEnd();
        recyclerView.setLayoutManager(linearLayoutManager);
        arrayList = new ArrayList<>();
        adapter = new ChatAdapter(arrayList, getContext(), this);
        recyclerView.setAdapter(adapter);

        this.inboxRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUserUid)
                .collection("inbox")
                .document(otherUserUid);

        checkIfInboxExist();

        view.findViewById(R.id.send_button).setOnClickListener(view1 -> {
            String sendText = messageEditText.getText().toString();
            if (!sendText.isEmpty()) {
                send(sendText);
                messageEditText.setText("");
            } else {
                Helper.customToast(getActivity(), "write something to send!");
            }
        });

        setUserProfileInfo(otherUserUid);
    }

    void send(String text) {
        if (isInboxExist) {
            HashMap<String, Object> textData = new HashMap<>();
            textData.put("timeStamp", FieldValue.serverTimestamp());
            textData.put("text", text);
            textData.put("userUid", currentUserUid);

            FirebaseFirestore.getInstance().collection("chats")
                    .document(this.chatId)
                    .collection("texts")
                    .document()
                    .set(textData)
                    .addOnSuccessListener(aVoid -> {
                    });

        } else {
            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
            DocumentReference currentUser = collectionReference.document(currentUserUid).collection("inbox").document(otherUserUid);
            DocumentReference otherUser = collectionReference.document(otherUserUid).collection("inbox").document(currentUserUid);
            setUpInbox(currentUser, otherUser, text);
        }
    }

    void checkIfInboxExist() {
        inboxRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().exists()) {
                    Log.d(TAG, "checkIfInboxExist: this exist");

                    this.isInboxExist = true;
                    this.chatId = (String) task.getResult().get("chatId");
                    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chats")
                            .document(this.chatId)
                            .collection("texts");
                    syncChats(collectionReference);

                } else {
                    this.isInboxExist = false;
                    Log.d(TAG, "checkIfInboxExist: this not exist");

                }
            }
        });

    }

    void setUpInbox(DocumentReference currentUserInboxRef, DocumentReference otherInboxRef, String text) {
        DocumentReference chatRef = FirebaseFirestore.getInstance().collection("chats")
                .document();
        //
        HashMap<String, Object> currentInboxData = new HashMap<>();
        currentInboxData.put("uid", otherUserUid);
        currentInboxData.put("chatId", chatRef.getId());

        currentUserInboxRef.set(currentInboxData)
                .addOnCompleteListener(task -> {

                    HashMap<String, Object> otherInboxData = new HashMap<>();
                    otherInboxData.put("uid", currentUserUid);
                    otherInboxData.put("chatId", chatRef.getId());

                    otherInboxRef.set(otherInboxData).addOnCompleteListener(task1 -> {
                        isInboxExist = true;
                        this.chatId = chatRef.getId();
//                        afterInboxCreated(chatRef.collection("texts"));
                        syncChats(chatRef.collection("texts"));
                        send(text);
                    });
                });


    }


    void syncChats(CollectionReference collectionReference) {
        collectionReference.orderBy("timeStamp",Query.Direction.DESCENDING).limit(limit)
                .addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            arrayList.clear();
            for (QueryDocumentSnapshot doc : value) {
                arrayList.add(doc);
                adapter.notifyDataSetChanged();
            }
            limit=limit+10;
        });

    }


    public Chat(String userUid) {
        this.otherUserUid = userUid;
        this.currentUserUid = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public void onBottomReached(Timestamp timestamp) {
        Log.d(TAG, "onBottomReached");
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chats")
                .document(this.chatId)
                .collection("texts");
        limit=limit+10;
        syncChats(collectionReference);

    }

    void setUserProfileInfo(String uid) {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("users").document(uid);

        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                textView.setText((String) document.get("name"));
                String imageUrl = (String) document.get("imageUrl");
                loadImage(imageUrl, imageView);
            }
        });
    }


    void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url)
                .error(R.drawable.profile_icon)
                .placeholder(R.drawable.profile_icon)
                .transform(new CircleTransform())
                .into(imageView);
    }
}
