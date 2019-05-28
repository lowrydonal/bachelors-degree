package com.lowry.lapspace3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * ACTIVITY: CAPTURE IMAGE TO VERIFY AND RETRIEVE TEMP IMAGE FROM DB
 */

public class GetImagesActivity extends AppCompatActivity {
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    Button btnVerifyPreview;
    ImageView previewImageView;
    ImageView previewTempImageView;
    String imageString; // retrieved from db
    int lap_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_images);

        // get intent
        lap_id = getIntent().getIntExtra("lap_id", -1);

        previewImageView = (ImageView) findViewById(R.id.iv2);
        previewTempImageView = (ImageView) findViewById(R.id.iv1);
        btnVerifyPreview = (Button) findViewById(R.id.btn_verify_preview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

//        // launch progress bar fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

//        if (displayPreview == false) {
//            FragmentProgressBar progressBarFragment = new FragmentProgressBar();
//            fragmentTransaction.replace(android.R.id.content, progressBarFragment);
//        } else {
//            DisplayImgPreviewFragment displayImgPreviewFragment = new DisplayImgPreviewFragment();
//            fragmentTransaction.replace(android.R.id.content, displayImgPreviewFragment);
//            fragmentTransaction.commit();
//        }
//        fragmentTransaction.commit();

        // launch camera intent
        new CaptureImageTask().execute();

        // retrieve temp image string from db
        new DatabaseCallTask().execute();

        // intent to verify image activity
        btnVerifyPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetImagesActivity.this, VerifyImageActivity.class);
                intent.putExtra("tempImgString", imageString);
                intent.putExtra("lap_id", lap_id);
                intent.putExtra("imageFilePath", mCurrentPhotoPath);
                GetImagesActivity.this.startActivity(intent);
            }
        });
    }


    //---------------------------------------METHODS---------------------------------------------//


    // DATABASE CALL ASYNC TASK
    private class DatabaseCallTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            retrieveTempImgString(); // string value assigned to imageString global var
            return null;
        }
    }


    // RETRIEVE TEMP IMAGE STRING FROM DATABASE
    public void retrieveTempImgString() {
        int company_id = getIntent().getIntExtra("company_id", -1);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String image_string = jsonObject.getString("image_string");
                        imageString = image_string; // global variable above
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GetImagesActivity.this);
                        builder.setMessage("LoginActivity Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestRetrieveTempImg retrieveTempImgRequest = new RequestRetrieveTempImg(company_id, responseListener);
        RequestQueue queue = Volley.newRequestQueue(GetImagesActivity.this);
        queue.add(retrieveTempImgRequest);
    }


    // CAPTURE IMAGE ASYNC TASK
    private class CaptureImageTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            dispatchTakePictureIntent();
            return null;
        }
    }


    // CAPTURE IMAGE INTENT LAUNCH
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }


    // CAMERA INTENT ON ACTIVITY RESULT
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // preview image to verify
            Bitmap bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath);
            bitmap1 = getResizedBitmap(bitmap1, 600);  // resize
            previewImageView.setImageBitmap(bitmap1);

            // preview temp image
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap bitmap2 = getResizedBitmap(decodedByte, 600);
            previewTempImageView.setImageBitmap(bitmap2);
        }
    }


    // CREATE FILE LOCATION FOR IMAGE CAPTURED
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    // RESIZE BITMAP WITH SAME RATIO
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}