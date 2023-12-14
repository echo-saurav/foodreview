package com.example.productreview.editProfile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.productreview.Helper;
import com.example.productreview.R;
import com.example.productreview.upload.UploadPicture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class EditProfile extends AppCompatActivity {
    private EditText nameEditText, bioEditText;
    final int PICK_IMAGE_REQ = 13290;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private Bitmap loadedBitmap;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.edit_profile);
        nameEditText = findViewById(R.id.name);
        bioEditText = findViewById(R.id.bio);
        imageView = findViewById(R.id.imageView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating ProfileFragment");
        progressDialog.setCancelable(false);
        //setting profile
        this.uid = FirebaseAuth.getInstance().getUid();
        nameEditText.setText(getIntent().getStringExtra("name"));
        bioEditText.setText(getIntent().getStringExtra("bio"));
        Picasso.get().load(getIntent().getStringExtra("imageUrl"))
                .error(R.drawable.profile_icon)
                .fit()
                .centerCrop()
                .into(imageView);

    }

    public void updateNewProfile(View view) {
        HashMap<String, Object> newUpdatedProfileData = new HashMap<>();
        newUpdatedProfileData.put("name", nameEditText.getText().toString());
        newUpdatedProfileData.put("bio", bioEditText.getText().toString());

        if (loadedBitmap != null) {
            UploadPicture uploadPicture = new UploadPicture(
                    FirebaseFirestore.getInstance().collection("users").document(uid),
                    null,
                    newUpdatedProfileData,
                    loadedBitmap,
                    progressDialog,
                    this,
                    this

            );
            uploadPicture.upload();
        } else {
            progressDialog.show();
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                    .update(newUpdatedProfileData).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Helper.customToast(EditProfile.this, "ProfileFragment Updated!");
                } else {
                    Helper.customToast(EditProfile.this, "Failed");
                }
            });
        }
    }

    //upload image
    public void chooseImage(View view) {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ);

        if(isStoragePermissionGranted()){
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQ);
        }
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQ) {

            // Get selected gallery image
            Uri selectedPicture = data.getData();
            // Get and resize profile image
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // TRY getactvity() as well if not work
            Cursor cursor = getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            loadedBitmap = BitmapFactory.decodeFile(picturePath);

            ExifInterface exif = null;
            try {
                File pictureFile = new File(picturePath);
                exif = new ExifInterface(pictureFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    loadedBitmap = rotateBitmap(loadedBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    loadedBitmap = rotateBitmap(loadedBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    loadedBitmap = rotateBitmap(loadedBitmap, 270);
                    break;
            }
            imageView.setImageBitmap(loadedBitmap);
        }
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 1) {

            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQ);
            //resume tasks needing this permission
        }

    }

}
