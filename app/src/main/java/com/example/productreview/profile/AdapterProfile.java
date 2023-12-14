package com.example.productreview.profile;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.viewHolder.ProfileProductViewHolder;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import static com.example.productreview.MainActivity.TAG;

public class AdapterProfile extends RecyclerView.Adapter<ProfileProductViewHolder> {
    private ArrayList<Object> arrayList;
    private Context context;
    private OnBottomReachedListener onBottomReachedListener;

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public AdapterProfile(ArrayList<Object> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_product_view, viewGroup, false);
        return new ProfileProductViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileProductViewHolder profileProductViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: position:"+position);
        if (position + 1 == getItemCount()) {
            Object object = arrayList.get(arrayList.size() - 1);
            DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
            Timestamp stamp = documentSnapshot.getTimestamp("timeStamp");
            onBottomReachedListener.onBottomReached(stamp);
        }
        profileProductViewHolder.setView(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}
