package com.example.productreview.viewHolder;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;


public class ChatViewHolder extends RecyclerView.ViewHolder {
    LinearLayout gravityLayout,textLayout;
    TextView textView;
    ImageView imageView;
    Context context;

    public ChatViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        gravityLayout=itemView.findViewById(R.id.gravity_layout);
        textLayout=itemView.findViewById(R.id.text_layout);
        textView=itemView.findViewById(R.id.text);
        this.context=context;
//        itemView=imageView.findViewById(R.id.imageView);
    }

    public void setView(Object object){
        DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
        String text=(String)documentSnapshot.getData().get("text");
        String userUid=(String)documentSnapshot.getData().get("userUid");
        if(userUid.equals(FirebaseAuth.getInstance().getUid())){
            textView.setTextColor(Color.BLACK);
            gravityLayout.setGravity(Gravity.END);
            textLayout.setBackground(context.getDrawable(R.drawable.chat_current_user));
        }else {
            textView.setTextColor(Color.WHITE);
            gravityLayout.setGravity(Gravity.START);
            textLayout.setBackground(context.getDrawable(R.drawable.chat_other_user));
        }
        textView.setText(text);

    }
}
