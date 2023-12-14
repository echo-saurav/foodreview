package com.example.productreview.upload;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.productreview.Helper;
import com.example.productreview.R;
import com.example.productreview.TestActivity;
import com.example.productreview.addRecipe.AddRecipe;
import com.example.productreview.camera.CameraActivity;
import com.example.productreview.userManagement.UserManagement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UploadActivity extends AppCompatActivity implements GetLocation.GetLocationUpdate {
    private static final String TAG = "UPLOAD";
    final int PICK_IMAGE_REQ = 13290;
    private Uri imageFileUri = null;
    ImageView imageView;
    EditText productDescription, companyName, locationEditText;
    AutoCompleteTextView productName;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    GetLocation getLocation;
    Location location;
    Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.upload_post);
        imageView = findViewById(R.id.upload_image);
        productDescription = findViewById(R.id.products_description);
        companyName = findViewById(R.id.company_name);
        locationEditText = findViewById(R.id.location);
        productName = findViewById(R.id.products_name);
        String[] countries = getResources().getStringArray(R.array.food_item_list);
        ArrayAdapter<String> autoComplateTextView = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,countries);
        productName.setAdapter(autoComplateTextView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setCancelable(false);

        //get locationEditText
        getLocation = new GetLocation(this, this);
        locationEditText.setHint("Getting Location...");
        if (checkGpsAndInternet()) {
            isLocationPermissionGranted();
        } else {
            Helper.customToast(this, "Please Turn on Location and Internet to auto detect location");
            locationEditText.setHint("Error getting location! Enter on your own");
        }

        //post button
        toolbar = findViewById(R.id.upload_option);
        toolbar.inflateMenu(R.menu.upload_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> {

            String proDes = productDescription.getText().toString();
            String company = companyName.getText().toString();
            String loc = locationEditText.getText().toString();
            String proName = productName.getText().toString();
            //if not given all value
            if (proDes.isEmpty() || company.isEmpty() || loc.isEmpty()) {
                Helper.customToast(UploadActivity.this, "Please add image and information");
            } else {
                uploadButton(proDes, proName, company, loc);
            }
            return false;
        });
        if(getIntent().getStringExtra("imageUri")!=null){

            Uri myUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            if(myUri!=null){
//                imageFileUri=myUri;
                imageFileUri=myUri;
//                bitmap=getBitmap(myUri);
            }

            if(imageFileUri!=null){

//                bitmap=getBitmap(imageFileUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    public void uploadButton(String proDes, String proName, String company, String loc) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {


//                    ;{proDes.toLowerCase(),proName.toLowerCase(),company.toLowerCase()};

            Map<String, Object> data = new HashMap<>();

            data.put("dataType",Helper.FOOD);
            data.put("userUid", user.getUid());
            data.put("productDescription", proDes);
            data.put("productName", proName);
            data.put("company", company);
            data.put("searchProductDescription", proDes.toLowerCase());
            data.put("searchProductName", proName.toLowerCase());
            data.put("searchCompany", company.toLowerCase());
            data.put("address", loc);
            data.put("timeStamp", FieldValue.serverTimestamp());
            data.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));

            UploadPicture uploadPicture = new UploadPicture(documentReference, documentReference.getId(), data, bitmap, progressDialog, this, this);
            uploadPicture.upload();
        }else {
            startActivity(new Intent(this, UserManagement.class));
            finish();
        }

    }


    //upload image
    public void chooseImage(View view) {
        if (isStoragePermissionGranted()) {
//            Log.d(TAG, "chooseImage: granted permission");
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ);

            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQ);

        } else {
            Log.d(TAG, "chooseImage: not granted");
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQ) {

            // Get selected gallery image
            Uri selectedPicture = data.getData();
            imageFileUri=data.getData();
            // Get and resize profile image
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // TRY getactvity() as well if not work
            Cursor cursor = getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            bitmap = BitmapFactory.decodeFile(picturePath);

            android.media.ExifInterface exif = null;
            try {
                File pictureFile = new File(picturePath);
                exif = new android.media.ExifInterface(pictureFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int orientation = android.media.ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateBitmap(bitmap, 90);
                    break;
                case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap= rotateBitmap(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap= rotateBitmap(bitmap, 270);
                    break;
            }
            imageView.setImageBitmap(bitmap);
        }
//        if (requestCode == PICK_IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Log.d("EditProfile", "onActivityResult: success");
//            imageFileUri = data.getData();
//            if (imageFileUri != null) {
//                try {
//                    //..First convert the Image to the allowable size so app do not throw Memory_Out_Bound Exception
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);
//                    int resolution = 900;
//                    options.inSampleSize = calculateInSampleSize(options, resolution  , resolution);
//                    options.inJustDecodeBounds = false;
//                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);
//
//                    //...Now You have the 'bitmap' to rotate....
//                    //...Rotate the bitmap to its original Orientation...
//
//                    //...After Rotation set the image to Image View...
//
//                    bitmap=ImageOrientation.modifyOrientation(getApplicationContext(),bitmap,imageFileUri);
//                    this.bitmap=bitmap;
//                    //
////                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFileUri);
//                    imageView.setImageBitmap(bitmap);
//
//                } catch (Exception e) {
//                }
//            }
//        }
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    public  Bitmap getBitmap(Uri selectedPicture){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        // TRY getactvity() as well if not work
        Cursor cursor = getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(picturePath);

        android.media.ExifInterface exif = null;
        try {
            File pictureFile = new File(picturePath);
            exif = new android.media.ExifInterface(pictureFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = android.media.ExifInterface.ORIENTATION_NORMAL;

        if (exif != null)
            orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateBitmap(bitmap, 90);
                break;
            case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                bitmap= rotateBitmap(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap= rotateBitmap(bitmap, 270);
                break;
        }
        return bitmap;
    }


    void isLocationPermissionGranted() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "isLocationPermissionGranted: all permissions");
                getLocation.updateLocation();
                return;
            } else {

                Log.d(TAG, "isLocationPermissionGranted: not granted");
                //get location permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 10);
            }
        }
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

//            Log.d("On permission result", "onRequestPermissionsResult: granted");
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ);
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQ);
            //resume tasks needing this permission
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 10) {
            getLocation.updateLocation();
        }
    }

    @Override
    public void currentLocation(Location location) {
        Log.d(TAG, "currentLocation: " + location.getLongitude() + " " + location.getLatitude());
        this.location = location;
        //get full address
        String address = getCompleteAddressString(location);
        if (!address.equals("")) {
            locationEditText.setText(address);
        }

    }

    private String getCompleteAddressString(Location location) {
        double LATITUDE = location.getLatitude();
        double LONGITUDE = location.getLongitude();
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    boolean checkGpsAndInternet() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {

            return false;
        }
        return true;
    }

    public void CaptureImage(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void goToAddRecipe(View view) {
        startActivity(new Intent(this, AddRecipe.class));
//        startActivity(new Intent(this, TestActivity.class));
    }
}
