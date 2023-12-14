package com.example.productreview.addRecipe;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.productreview.Helper;
import com.example.productreview.R;
import com.example.productreview.camera.CameraActivity;
import com.example.productreview.upload.GetLocation;
import com.example.productreview.upload.ImageOrientation;
import com.example.productreview.upload.UploadActivity;
import com.example.productreview.upload.UploadPicture;
import com.example.productreview.userManagement.UserManagement;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

import static com.example.productreview.upload.UploadActivity.calculateInSampleSize;

public class AddRecipe extends AppCompatActivity  implements GetLocation.GetLocationUpdate{
    static ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    static RichEditor ingredientsTextView;
    static TextView preparationTextView;
    final int PICK_IMAGE_REQ = 13290;
    Location location;
    EditText locationEditText,recipeName;
    IngredientsFragment ingredientsFragment;
    PreparationFragment preparationFragment;
    GetLocation getLocation;
    static ArrayList<RecipeFragment> fragmentArrayList;
    ProgressDialog progressDialog;
    Bitmap bitmap;
    private Uri imageFileUri = null;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.add_recipe_activity);
        imageView = findViewById(R.id.upload_image);
        recipeName=findViewById(R.id.recipe_name);
        toolbar = findViewById(R.id.recipe_toolbar);
        toolbar.inflateMenu(R.menu.upload_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            uploadRecipe();
            return false;
        });
        preparationFragment = new PreparationFragment();
        ingredientsFragment = new IngredientsFragment();
        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(ingredientsFragment);
        fragmentArrayList.add(preparationFragment);

        locationEditText = findViewById(R.id.location);
        viewPager = findViewById(R.id.recipe_view_pager);
        tabLayout = findViewById(R.id.tab);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        getLocation = new GetLocation(this, this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setCancelable(false);

        getLocation = new GetLocation(this, this);
        locationEditText.setHint("Getting Location...");
        if (checkGpsAndInternet()) {
            isLocationPermissionGranted();
        } else {
            Helper.customToast(this, "Please Turn on Location and Internet to auto detect location");
            locationEditText.setHint("Error getting location! Enter on your own");
        }


        if(getIntent().getStringExtra("imageUri")!=null){

            Uri myUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            if(myUri!=null){
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

    void uploadRecipe() {
        if(!ingredientsTextView.getHtml().isEmpty() && !preparationTextView.getText().toString().isEmpty() && !recipeName.getText().toString().isEmpty()){
            upload(ingredientsTextView.getHtml(),
                    recipeName.getText().toString(),
                    preparationTextView.getText().toString());
//                    locationEditText.getText().toString());
        }else {
            Helper.customToast(this,"Please fill all the fields");
        }
//        if(!ingredientsTextView.getText().toString().isEmpty() && !preparationTextView.getText().toString().isEmpty() && !recipeName.getText().toString().isEmpty()){
//            upload(ingredientsTextView.getText().toString(),
//                    recipeName.getText().toString(),
//                    preparationTextView.getText().toString(),
//                    locationEditText.getText().toString());
//        }

    }
    public void upload(String ingredients, String recipeName, String preparation) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("recipe").document();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {


            Map<String, Object> data = new HashMap<>();
            data.put("DataType",Helper.RECIPE);
            data.put("userUid", user.getUid());
            data.put("ingredients", ingredients);
            data.put("recipeName", recipeName);
            data.put("searchRecipeName", recipeName.toLowerCase());
            data.put("preparation", preparation);
//            data.put("address", address);
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
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ);

            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQ);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
//                Log.d("EditProfile", "onActivityResult: not null");
//                try {
//                    //..First convert the Image to the allowable size so app do not throw Memory_Out_Bound Exception
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);
//                    int resolution = 500;
//                    options.inSampleSize = calculateInSampleSize(options, resolution  , resolution);
//                    options.inJustDecodeBounds = false;
//                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);
//
//                    //...Now You have the 'bitmap' to rotate....
//                    //...Rotate the bitmap to its original Orientation...
//
//                    //...After Rotation set the image to Image View...
//
//                    bitmap= ImageOrientation.modifyOrientation(getApplicationContext(),bitmap,imageFileUri);
//                    this.bitmap=bitmap;
////                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFileUri);
//                    imageView.setImageBitmap(bitmap);
//                } catch (Exception e) {
//                    Log.d("Test", "onActivityResult: "+e);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 1) {

//            Log.d("On permission result", "onRequestPermissionsResult: granted");
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ);
            //resume tasks needing this permission
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQ);
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 10) {
            getLocation.updateLocation();
        }
    }
    public boolean isStoragePermissionGranted() {
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

    public static void onNextClick() {
        if (viewPager.getCurrentItem() < fragmentArrayList.size()) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    @Override
    public void currentLocation(Location location) {
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
    void isLocationPermissionGranted() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation.updateLocation();
                return;
            } else {
                //get location permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 10);
            }
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentArrayList.get(position).getTitle();
        }
    }



    public static class IngredientsFragment extends RecipeFragment {
        String title = "Ingredients";


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.ingredients_fragment, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ingredientsTextView = view.findViewById(R.id.ingredients_edit_text);
//            ingredientsTextView=view.findViewById(R.id.ingredients_edit_text);
            ingredientsTextView.setOnInitialLoadListener(new RichEditor.AfterInitialLoadListener() {
                @Override
                public void onAfterInitialLoad(boolean isReady) {
                    if(isReady){

                        Log.d("ss", "onAfterInitialLoad: ");
                        ingredientsTextView.focusEditor();
                        ingredientsTextView.setFontSize(4);
                        ingredientsTextView.setBullets();
                        ingredientsTextView.setFontSize(4);

                    }
                }
            });


            view.findViewById(R.id.next_click_button).setOnClickListener(view1 -> {
                onNextClick();
            });
        }

        @Override
        public String getTitle() {
            return this.title;
        }
    }

    public static class PreparationFragment extends RecipeFragment {
        String title = "Preparation";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.preparation_fragment, container, false);
        }


        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            preparationTextView = view.findViewById(R.id.preparation_edit_text);
            view.findViewById(R.id.next_click_button).setOnClickListener(view1 -> {
                onNextClick();
            });
        }

        @Override
        public String getTitle() {
            return this.title;
        }
    }
    public void CaptureImage(View view) {
        startActivity(new Intent(this, CameraActivity.class)
                .putExtra("from","recipe")
        );
    }

}

