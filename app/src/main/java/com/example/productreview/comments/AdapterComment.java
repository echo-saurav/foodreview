package com.example.productreview.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.R;
import com.example.productreview.adapter.CustomHashMap;
import com.example.productreview.viewHolder.CommentViewHolder;
import com.google.firebase.database.DataSnapshot;

public class AdapterComment extends RecyclerView.Adapter<CommentViewHolder> {
    private CustomHashMap<String, DataSnapshot> commentValueHolderCustomHashMap;
    private Context context;

    public AdapterComment(CustomHashMap<String, DataSnapshot> commentValueHolderCustomHashMap, Context context) {
        this.commentValueHolderCustomHashMap = commentValueHolderCustomHashMap;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_comment, viewGroup, false);
        return new CommentViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        commentViewHolder.setView(commentValueHolderCustomHashMap.getValueByIndex(i));
    }

    @Override
    public int getItemCount() {
        return commentValueHolderCustomHashMap.size();
    }
}
