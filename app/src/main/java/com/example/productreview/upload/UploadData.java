package com.example.productreview.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;


import androidx.annotation.NonNull;

import com.example.productreview.Helper;
import com.example.productreview.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UploadData implements OnCompleteListener<Void> {
    private String imageUrl;
    private Map<String, Object> data;
    private DocumentReference documentReference;
    private ProgressDialog progressDialog;
    private UploadPicture uploadPicture;
    private Activity activity;
    private String postId;

    public UploadData(DocumentReference documentReference, String postId, String imageUrl, Map<String, Object> data, ProgressDialog progressDialog, Activity activity) {
        this.postId=postId;
        this.imageUrl = imageUrl;
        this.data = data;
        this.progressDialog = progressDialog;
        this.activity = activity;
        this.documentReference=documentReference;
    }

    public void run(UploadPicture uploadPicture) {
        this.uploadPicture = uploadPicture;
        data.put("imageUrl", imageUrl);
        documentReference.set(data).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {

            progressDialog.dismiss();
            Helper.customToast(activity, "Post Uploaded!");
            activity.finish();
            activity.startActivity(new Intent(activity, MainActivity.class));
        } else {
            //if we can't store data we will delete image
            uploadPicture.deleteImage();
            Helper.customToast(activity, task.getException().getMessage());
        }

    }


}
