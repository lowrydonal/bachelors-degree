package com.lowry.lapspace3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileCoActivity extends AppCompatActivity {

    String imageString;
    EditText etName;
    EditText etEmail;
    EditText etDemDesc;
    TextView uploadedImTV;
    String current_image_string;
    int id;
    static final int REQUEST_CHOOSE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_co);

        // get data from intent
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        final String name = intent.getStringExtra("name");
        final String email = intent.getStringExtra("email");
        final String demographic_desc = intent.getStringExtra("demographic_desc");

        etName = (EditText) findViewById(R.id.et_co_name);
        etEmail = (EditText) findViewById(R.id.et_co_email);
        etDemDesc = (EditText) findViewById(R.id.et_demographic_desc);
        uploadedImTV = (TextView) findViewById(R.id.uploaded_im_tv);
        final Button btnConfirm = (Button) findViewById(R.id.btn_co_edit);
        final Button btnUpload = (Button) findViewById(R.id.btn_upload);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_co);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_close_white_18dp);

        etName.setText(name);
        etEmail.setText(email);
        etDemDesc.setText(demographic_desc);

        // confirm button on click
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard when login button pressed
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                // make database call
                new DbCallTask().execute();
            }
        });

        // upload button on click
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch gallery intent
                new GalleryIntentTask().execute();
            }
        });
    }


    //------------------------------------------METHODS-------------------------------------------//


    // GALLERY INTENT ASYNC TASK
    private class GalleryIntentTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            dispatchTakePictureIntent();
            return null;
        }
    }

    // GALLERY INTENT
    public void dispatchTakePictureIntent() {
        Intent intent = new Intent();
        intent.setType("image/*"); // only images
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_PHOTO);
    }


    // CONVERT BITMAP TO IMAGE STRING FOR UPLOAD TO DB
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = getResizedBitmap(bitmap, 600);  // resize bitmap
                uploadedImTV.setText("File uploaded");
                // convert bitmap to base 64 string
                ByteArrayOutputStream byteArrayOutputStreamObject = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
                byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
                imageString = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


    // DB CALL AND PROGRESS FRAGMENT START ASYNC TASK
    private class DbCallTask extends AsyncTask<Void, Void, Void> {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        FragmentProgressBar progressBarFragment;

        @Override
        protected void onPreExecute() {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            progressBarFragment = new FragmentProgressBar();
            fragmentTransaction.replace(android.R.id.content, progressBarFragment);
            fragmentTransaction.commit();
        }
        @Override
        protected Void doInBackground(Void... params) {
            dbCall();
            return null;
        }
    }


    // DB CALL
    public void dbCall() {
        final int company_id = id;
        final String name = etName.getText().toString();
        final String email = etEmail.getText().toString();
        final String demographic_desc = etDemDesc.getText().toString();
        final String image_string;
        if (imageString == null) {
            image_string = current_image_string;
        } else {
            image_string = imageString;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Toast.makeText(EditProfileCoActivity.this, "Editing Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfileCoActivity.this, ProfileLapActivity.class);
                        intent.putExtra("id", company_id);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        intent.putExtra("image_string", image_string);
                        intent.putExtra("demographic_desc", demographic_desc);
                        EditProfileCoActivity.this.startActivity(intent);
                    } else {
                        Toast.makeText(EditProfileCoActivity.this, "Editing Failed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfileCoActivity.this, EditProfileCoActivity.class);
                        EditProfileCoActivity.this.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Add the request to the RequestQueue.
        RequestEditDetailsCo editRequest = new RequestEditDetailsCo(id, name, email, image_string,
                demographic_desc, responseListener);
        RequestQueue queue = Volley.newRequestQueue(EditProfileCoActivity.this);
        queue.add(editRequest);
    }


    // ON BACK BUTTONS PRESSED
    // fake back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(EditProfileCoActivity.this, LoginActivity.class);
                EditProfileCoActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileCoActivity.this, LoginActivity.class);
        EditProfileCoActivity.this.startActivity(intent);
    }
}
