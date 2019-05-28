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
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// ACTIVITY: LAPTOP OWNER REGISTRATION FORM

public class RegisterLapActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static final int REQUEST_CHOOSE_PHOTO = 1;
    String imageString;
    String age_range;
    String county;
    EditText etName;
    EditText etEmail;
    TextView uploadedImTV;
    EditText etPassword;
    EditText etOccupation;
    EditText etLaptopLoc;
    EditText etAddressLine1;
    EditText etAddressLine2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_lap);

        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        etOccupation = (EditText) findViewById(R.id.et_occupation);
        etLaptopLoc = (EditText) findViewById(R.id.et_laptop_loc);
        etAddressLine1 = (EditText) findViewById(R.id.et_address_line1);
        etAddressLine2 = (EditText) findViewById(R.id.et_address_line2);
        uploadedImTV = (TextView) findViewById(R.id.uploaded_im_tv);
        final Button btnRegister = (Button) findViewById(R.id.btn_register);
        final Button btnUpload = (Button) findViewById(R.id.btn_upload);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_reg_lap);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_close_white_18dp);


        // init age range spinner
        Spinner ageRangeSpinner = (Spinner) findViewById(R.id.age_range_spinner);
        ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(this, R.array.age_range_array, android.R.layout.simple_spinner_item);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageRangeSpinner.setAdapter(ageAdapter);
        ageRangeSpinner.setOnItemSelectedListener(this);

        // init county spinner
        Spinner countySpinner = (Spinner) findViewById(R.id.county_spinner);
        ArrayAdapter<CharSequence> countyAdapter = ArrayAdapter.createFromResource(this, R.array.county_array, android.R.layout.simple_spinner_item);
        countyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpinner.setAdapter(countyAdapter);
        countySpinner.setOnItemSelectedListener(this);

        // register button on click
        btnRegister.setOnClickListener(new View.OnClickListener() {
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
        // if all required fields filled
        if (!TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etEmail.getText())
                && !TextUtils.isEmpty(etPassword.getText()) && !TextUtils.isEmpty(etOccupation.getText())
                && !TextUtils.isEmpty(etLaptopLoc.getText()) && !TextUtils.isEmpty(etAddressLine1.getText())) {

            final String name = etName.getText().toString();
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();
            final String occupation = etOccupation.getText().toString();
            final String laptop_loc = etLaptopLoc.getText().toString();
            final String address_line1 = etAddressLine1.getText().toString();
            final String address_line2 = etAddressLine2.getText().toString();
            final String image_string = imageString;

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            Intent intent = new Intent(RegisterLapActivity.this, LoginActivity.class);
                            RegisterLapActivity.this.startActivity(intent);
                        } else {
                            Toast.makeText(RegisterLapActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterLapActivity.this, RegisterLapActivity.class);
                            RegisterLapActivity.this.startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            // Add the request to the RequestQueue.
            RequestRegisterLap registerRequest = new RequestRegisterLap(name, email, password, occupation, age_range,
                    image_string, laptop_loc, address_line1, address_line2, county, responseListener);
            RequestQueue queue = Volley.newRequestQueue(RegisterLapActivity.this);
            queue.add(registerRequest);
        }

        // else if not all required fields filled: mandatory field error
        else if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Name is required");
        } else if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("Email is required");
        } else if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError("Password is required");
        } else if (TextUtils.isEmpty(etOccupation.getText())) {
            etOccupation.setError("Occupation is required");
        } else if (TextUtils.isEmpty(etLaptopLoc.getText())) {
            etLaptopLoc.setError("This field is required");
        } else if (TextUtils.isEmpty(etAddressLine1.getText())) {
            etAddressLine1.setError("This field is required");
        }
    }


    // SPINNER METHODS
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.age_range_spinner:
                age_range = parent.getItemAtPosition(position).toString();
                break;
            case R.id.county_spinner:
                county = parent.getItemAtPosition(position).toString();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    // ON BACK BUTTON PRESSED
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(RegisterLapActivity.this, LoginActivity.class);
                RegisterLapActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterLapActivity.this, LoginActivity.class);
        RegisterLapActivity.this.startActivity(intent);
    }
}
