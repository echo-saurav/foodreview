package com.example.productreview.viewHolder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.FragmentManagement;
import com.example.productreview.MainActivity;
import com.example.productreview.R;
import com.example.productreview.messages.Chat;
import com.example.productreview.messages.InboxValueHolder;
import com.example.productreview.roundImage.CircleTransform;
import com.squareup.picasso.Picasso;

import static com.example.productreview.MainActivity.TAG;

public class InboxViewHolder extends RecyclerView.ViewHolder {

    private FragmentManagement fragmentManagement;
    private TextView heading;
    private ImageView imageView;
    private View view;
    public InboxViewHolder(@NonNull View itemView,Context context) {
        super(itemView);
        this.fragmentManagement = (MainActivity) context;
        this.imageView=itemView.findViewById(R.id.imageView);
        this.heading=itemView.findViewById(R.id.heading);
        this.view=itemView;

    }

    public void setView(InboxValueHolder inboxValueHolder){
        Picasso.get().load(inboxValueHolder.getImageUrl()).transform(new CircleTransform()).into(imageView);
        heading.setText(inboxValueHolder.getUserName());

        Log.d(TAG, "setView: "+inboxValueHolder.toString());
        this.view.setOnClickListener(view -> {
            fragmentManagement.showFragment(new Chat(inboxValueHolder.getUid()));
        });
    }
}
