package com.example.productreview.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.viewHolder.ChatViewHolder;
import com.example.productreview.viewHolder.InboxViewHolder;

import java.util.ArrayList;

public class InboxAdapter extends RecyclerView.Adapter<InboxViewHolder> {

    private ArrayList<InboxValueHolder> arrayList;
    private Context context;
    private OnBottomReachedListener onBottomReachedListener;

    public InboxAdapter(ArrayList<InboxValueHolder> arrayList, Context context, OnBottomReachedListener onBottomReachedListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view, parent, false);
        return new InboxViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        holder.setView(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
