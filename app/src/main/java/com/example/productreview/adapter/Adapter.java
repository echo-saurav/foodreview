package com.example.productreview.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.OnBottomReachedListener;
import com.example.productreview.R;
import com.example.productreview.viewHolder.InboxViewHolder;
import com.example.productreview.viewHolder.LoadingViewHolder;
import com.example.productreview.viewHolder.ChatViewHolder;
import com.example.productreview.viewHolder.NotificationViewHolder;
import com.example.productreview.viewHolder.ProductViewHolder;
import com.example.productreview.viewHolder.ProfileProductViewHolder;
import com.example.productreview.viewHolder.RecipeViewHolder;
import com.example.productreview.viewHolder.SearchItemViewHolder;
import com.example.productreview.viewHolder.SponsorAdsViewHolder;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import static com.example.productreview.MainActivity.TAG;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Object> arrayList;
    private Context context;
    private OnBottomReachedListener onBottomReachedListener;
    //
    private int VIEW_TYPE;
    public static int PROFILE_VIEW_TYPE = 10;
    public static int FEED_VIEW_TYPE = 20;
    public static int SEARCH_VIEW_TYPE = 30;
    public static int NOTIFICATION_VIEW_TYPE = 40;
    public static int RECIPE_VIEW_TYPE = 70;
    public static int CHAT_VIEW_TYPE =100;
    public static int INBOX_VIEW_TYPE=200;

    private int SPONSOR_ADS_VIEW_TYPE = 50;
    private int LOADING_VIEW_TYPE = 60;
    //
    private boolean loadingVisibility = false;
    private int sponsorAdInterval = 6;


    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public Adapter(ArrayList<Object> arrayList, int VIEW_TYPE, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.VIEW_TYPE = VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == LOADING_VIEW_TYPE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_view, viewGroup, false);
            return new LoadingViewHolder(view);
        }

        if (viewType == RECIPE_VIEW_TYPE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_item_view, viewGroup, false);
            return new RecipeViewHolder(view);
        }

        if (viewType == SPONSOR_ADS_VIEW_TYPE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sponsor_item_view, viewGroup, false);
            return new SponsorAdsViewHolder(view);
        }

        if (viewType == FEED_VIEW_TYPE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_view, viewGroup, false);
            return new ProductViewHolder(view, context);
        }

        if (viewType == PROFILE_VIEW_TYPE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_product_view, viewGroup, false);
            return new ProfileProductViewHolder(view, context);
        }

        if (viewType == SEARCH_VIEW_TYPE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_view, viewGroup, false);
            return new SearchItemViewHolder(view, context);
        }

        if (viewType == NOTIFICATION_VIEW_TYPE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_view, viewGroup, false);
            return new NotificationViewHolder(view, context);
        }

        if (viewType== CHAT_VIEW_TYPE){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item_view, viewGroup, false);
            return new ChatViewHolder(view,context);
        }

        if (viewType==INBOX_VIEW_TYPE){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_view, viewGroup, false);
            return new InboxViewHolder(view,context);
        }


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ProductViewHolder) {
            ((ProductViewHolder) viewHolder).setView(arrayList.get(getCustomValuePosition(position)),this);
        }
        if (viewHolder instanceof ProfileProductViewHolder) {
            ((ProfileProductViewHolder) viewHolder).setView(arrayList.get(position));
        }
        if (viewHolder instanceof SearchItemViewHolder) {
            ((SearchItemViewHolder) viewHolder).setView(arrayList.get(position));
        }
        if (viewHolder instanceof NotificationViewHolder) {
            ((NotificationViewHolder) viewHolder).setView(arrayList.get(position));
        }
        if (viewHolder instanceof RecipeViewHolder) {
            ((RecipeViewHolder) viewHolder).setView(arrayList.get(position));
        }

        if (viewHolder instanceof SponsorAdsViewHolder) {
            ((SponsorAdsViewHolder) viewHolder).setView(getAdPosition() - 1);
        }

        if(viewHolder instanceof ChatViewHolder){
            Log.d(TAG, "onBindViewHolder: adding chats");
            Log.d(TAG, "onBindViewHolder: size"+arrayList.size());
            ((ChatViewHolder) viewHolder).setView(arrayList.get(position));
        }

        if (viewHolder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) viewHolder).setVisibility(loadingVisibility);
        }


        //load more item
        if (position + 1 == getItemCount() && VIEW_TYPE != NOTIFICATION_VIEW_TYPE && VIEW_TYPE!= CHAT_VIEW_TYPE && VIEW_TYPE!=INBOX_VIEW_TYPE ) {
            Object object = arrayList.get(arrayList.size() - 1);
            DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
            Timestamp stamp = documentSnapshot.getTimestamp("timeStamp");
            onBottomReachedListener.onBottomReached(stamp);

            Log.d(TAG, "onBindViewHolder: unxpected");
        }
        //load more item in profile view
        if (VIEW_TYPE == PROFILE_VIEW_TYPE && arrayList.size() == position + 1) {
            Log.d(TAG, "onBindViewHolder: position:" + position);
            Object object = arrayList.get(arrayList.size() - 1);
            DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
            Timestamp stamp = documentSnapshot.getTimestamp("timeStamp");
            onBottomReachedListener.onBottomReached(stamp);
            Log.d(TAG, "onBindViewHolder: unxpected2");
        }
    }

    int getCustomValuePosition(int recyclerViewPosition) {
        if (VIEW_TYPE != FEED_VIEW_TYPE) {
            return recyclerViewPosition;
        }
        int totalAdsPast = recyclerViewPosition / sponsorAdInterval;
        return recyclerViewPosition - totalAdsPast;
    }


    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && VIEW_TYPE!=CHAT_VIEW_TYPE && VIEW_TYPE!=RECIPE_VIEW_TYPE) {
            Log.d(TAG, "getItemViewType: this is loading view");
            return LOADING_VIEW_TYPE;
        }

        if (position + 1 == getItemCount() && VIEW_TYPE!=RECIPE_VIEW_TYPE) {
            return LOADING_VIEW_TYPE;
        }
        if (position % sponsorAdInterval == 0 &&
                position != 0 &&
                VIEW_TYPE == FEED_VIEW_TYPE) {
            return SPONSOR_ADS_VIEW_TYPE;
        }
        return VIEW_TYPE;
    }

    int getAdPosition() {
        return arrayList.size() / sponsorAdInterval;
    }

    @Override
    public int getItemCount() {
        if (VIEW_TYPE != FEED_VIEW_TYPE) {
            Log.d("SSSS", "getItemCount: "+arrayList.size());
            return arrayList.size();
        }
        int arraySize = arrayList.size();
        if (arraySize == 0) {
            Log.d(TAG, "getItemCount: 0");
            return 0;
        }
        int howManyAdsCanFit = arraySize / sponsorAdInterval;
        Log.d(TAG, "getItemCount: "+arraySize+howManyAdsCanFit+1);
        return arraySize + howManyAdsCanFit + 1;


    }

    public void setLoadingVisibility(boolean loadingVisibility) {
        this.loadingVisibility = loadingVisibility;
    }

    public void onDelete(Object object){
        arrayList.remove(object);
        this.notifyDataSetChanged();
    }
}