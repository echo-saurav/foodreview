package com.example.productreview;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Helper {

    public static int FOOD=1;
    public static int RECIPE=2;
    public static int USER=3;



    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }


    public static void customToast(Activity activity,String text){
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.cutom_toast_view, null);

        TextView textView=layout.findViewById(R.id.text_view_custom_toast);
        textView.setText(text);
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    public static void loadImage(StorageReference storageReference, final ImageView imageView){
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().toString();
                    Picasso.get().load(imageUrl)
                            .error(R.drawable.profile_icon)
                            .fit()
                            .centerCrop()
                            .into(imageView);
                }
            }
        });
    }

}
