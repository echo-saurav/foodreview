package com.example.productreview.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productreview.R;
import com.example.productreview.home.HomeFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class SponsorAdsViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView heading;
    private View view;

    public SponsorAdsViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageView);
        heading=itemView.findViewById(R.id.heading);
        view=itemView;
    }

    public void setView(int position){
      Object object=HomeFragment.getObject();
      if(object==null){
          setVisibility(false);
      }else {
          DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
          String imageUrl=(String) documentSnapshot.get("imageUrl");
          Picasso.get().load(imageUrl).fit().centerCrop().into(imageView);

          setVisibility(true);

      }

    }

    public void setVisibility(boolean isVisible){
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)view.getLayoutParams();
        if (isVisible){
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        }else{
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }
}
