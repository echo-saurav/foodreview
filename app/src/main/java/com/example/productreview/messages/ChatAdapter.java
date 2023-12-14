package com.example.productreview.messages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.adapter.CustomHashMap;
import com.example.productreview.viewHolder.ChatViewHolder;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import static com.example.productreview.MainActivity.TAG;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private ArrayList<Object> arrayList;
    private Context context;
    private OnBottomReachedListener onBottomReachedListener;

    public ChatAdapter(ArrayList<Object> arrayList, Context context, OnBottomReachedListener onBottomReachedListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_view, parent, false);
        return new ChatViewHolder(view, context);

    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.setView(arrayList.get(position));
        Log.d(TAG, "onBindViewHolder: position:"+position);
        //load more item
        if (position + 1 == getItemCount() ) {
            Object object = arrayList.get(arrayList.size() - 1);
            DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
            Timestamp stamp = documentSnapshot.getTimestamp("timeStamp");
            onBottomReachedListener.onBottomReached(stamp);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
