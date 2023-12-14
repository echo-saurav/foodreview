package com.example.productreview.viewHolder;

import android.content.Context;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FragmentManagement;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.comments.CommentValueHolder;
import com.example.productreview.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    private TextView userName, commentText, time;
    private SimpleDateFormat formatter;
    private FragmentManagement fragmentManagement;
    private Context context;

    public CommentViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        fragmentManagement = (MainActivity) context;
        userName = itemView.findViewById(R.id.userName);
        commentText = itemView.findViewById(R.id.comment_text);
        time = itemView.findViewById(R.id.time_stamp);
        formatter = new SimpleDateFormat("hh:mm  dd/MM/yyyy", Locale.ENGLISH);
    }

    public void setView(DataSnapshot snapshot) {
        final CommentValueHolder valueHolder = snapshot.getValue(CommentValueHolder.class);
        if (valueHolder != null) {
            userName.setText(valueHolder.getUserName());
            commentText.setText(valueHolder.getCommentText());
            time.setText(formatter.format(valueHolder.time));

            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fragment fragment;
                    if (valueHolder.getUid().equals(FirebaseAuth.getInstance().getUid())) {

                        fragment = ProfileFragment.newInstance(fragmentManagement);
                        Log.d("Products Click", "onClick: own profile");
                        fragmentManagement.showFragment(fragment);
                    }

                    fragment = ProfileFragment.newInstance(valueHolder.getUid(),fragmentManagement);
                    Log.d("Products Click", "onClick: others profile");
                    fragmentManagement.showFragment(fragment);
                }
            });

        }
    }


}
