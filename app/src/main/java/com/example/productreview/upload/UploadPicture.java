package com.example.productreview.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;


import androidx.annotation.NonNull;

import com.example.productreview.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Random;

import static com.example.productreview.upload.UploadActivity.calculateInSampleSize;

public class UploadPicture implements OnSuccessListener<UploadTask.TaskSnapshot>,
        OnFailureListener,
        OnProgressListener<UploadTask.TaskSnapshot>,
        OnCompleteListener<Uri> {

    private String postId;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    private Activity activity;
    private Context context;
    private Map<String, Object> data;
    private StorageReference storageReference;
    private DocumentReference documentReference;

    public UploadPicture(DocumentReference documentReference, String postId, Map<String, Object> data, Bitmap bitmap, ProgressDialog progressDialog, Activity activity, Context context) {
        this.postId = postId;
        this.bitmap = bitmap;
        this.progressDialog = progressDialog;
        this.activity = activity;
        this.context = context;
        this.data = data;
        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + postId);
        this.documentReference=documentReference;

    }

    public void upload() {
        if (bitmap != null) {
            progressDialog.show();
            new MyTask().execute(this.bitmap);

        } else {
            Helper.customToast(activity, "Please choose image or a video!");
        }
    }

    void run(Uri uri) {
        progressDialog.setMessage("Start uploading image...");
        storageReference.putFile(uri).addOnSuccessListener(this).addOnProgressListener(this).addOnFailureListener(this);
    }


    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
        progressDialog.setMessage("Upload " + (int) progress + "%");
    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        storageReference.getDownloadUrl().addOnCompleteListener(this);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        progressDialog.dismiss();
        Helper.customToast(activity, e.getMessage());
    }

    @Override
    public void onComplete(@NonNull Task<Uri> task) {
        if (task.isSuccessful()) {
            String imageUrl = task.getResult().toString();
            UploadData uploadData = new UploadData(documentReference,postId,imageUrl, data, progressDialog, activity);
            uploadData.run(this);
        } else {
            Helper.customToast(activity, task.getException().getMessage());
            progressDialog.dismiss();
            //if we can't have image url, we will delete image
            deleteImage();
        }
    }


    class MyTask extends AsyncTask<Bitmap, Void, Uri> {

        @Override
        protected Uri doInBackground(Bitmap... bitmaps) {
            progressDialog.setMessage("resizing images...");
            return getImageUri(context, resize(bitmaps[0], 900, 900));
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            run(uri);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
            Helper.customToast(activity, "Some error happen! Try again");
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ""+new Random().nextInt(), null);
        return Uri.parse(path);
    }

//    public Uri getImageUri(Context context, Bitmap mBitmap) {
//        Uri uri = null;
//        try {
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            // Calculate inSampleSize
//            options.inSampleSize = calculateInSampleSize(options, 900, 900);
//
//            // Decode bitmap with inSampleSize set
//            options.inJustDecodeBounds = false;
//            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 900, 900,
//                    true);
//            File file = new File(context.getFilesDir(), "Image"
//                    + new Random().nextInt() + ".jpeg");
//            FileOutputStream out = context.openFileOutput(file.getName(),
//                    Context.MODE_WORLD_READABLE);
//            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//            //get absolute path
//            String realPath = file.getAbsolutePath();
//            File f = new File(realPath);
//            uri = Uri.fromFile(f);
//
//        } catch (Exception e) {
//            Log.e("Your Error Message", e.getMessage());
//        }
//        return uri;
//    }

//    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) >= reqHeight
//                    && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public void deleteImage() {
        storageReference.delete();
    }

}
