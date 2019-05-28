package com.lowry.lapspace3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
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

public class EditProfileLapActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String imageString;
    String age_range;
    String county;
    EditText etName;
    EditText etEmail;
    TextView uploadedImTV;
    EditText etPassword;
    String current_image_string;
    EditText etOccupation;
    EditText etLaptopLoc;
    EditText etAddressLine1;
    EditText etAddressLine2;
    int id;
    int live_campaign;
    int company_id;
    static final int REQUEST_CHOOSE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_lap);

        // get data from intent
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        final String name = intent.getStringExtra("name");
        final String email = intent.getStringExtra("email");
        final String occupation = intent.getStringExtra("occupation");
        final String age_range = intent.getStringExtra("age_range");
        current_image_string = intent.getStringExtra("image_string");
        final String laptop_loc = intent.getStringExtra("laptop_loc");
        final String address_line1 = intent.getStringExtra("address_line1");
        final String address_line2 = intent.getStringExtra("address_line2");
        final String county = intent.getStringExtra("county");
        live_campaign = intent.getIntExtra("live_campaign", -1);
        company_id = intent.getIntExtra("company_id", -1);

        etName = (EditText) findViewById(R.id.et_name_edit_lap);
        etEmail = (EditText) findViewById(R.id.et_email_edit_lap);
        etOccupation = (EditText) findViewById(R.id.et_occupation_edit_lap);
        etLaptopLoc = (EditText) findViewById(R.id.et_laptop_loc_edit_lap);
        etAddressLine1 = (EditText) findViewById(R.id.et_address_line1_edit_lap);
        etAddressLine2 = (EditText) findViewById(R.id.et_address_line2_edit_lap);
        uploadedImTV = (TextView) findViewById(R.id.uploaded_im_tv_edit_lap);
        final Button btnConfirm = (Button) findViewById(R.id.btn_edit_lap);
        final Button btnUpload = (Button) findViewById(R.id.btn_upload_edit_lap);

        etName.setText(name);
        etEmail.setText(email);
        etOccupation.setText(occupation);
        etLaptopLoc.setText(laptop_loc);
        etAddressLine1.setText(address_line1);
        etAddressLine2.setText(address_line2);

        // init age range spinner
        Spinner ageRangeSpinner = (Spinner) findViewById(R.id.age_range_spinner_edit_lap);
        ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(this, R.array.age_range_array, android.R.layout.simple_spinner_item);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageRangeSpinner.setAdapter(ageAdapter);
        ageRangeSpinner.setOnItemSelectedListener(this);

        // init county spinner
        Spinner countySpinner = (Spinner) findViewById(R.id.county_spinner_edit_lap);
        ArrayAdapter<CharSequence> countyAdapter = ArrayAdapter.createFromResource(this, R.array.county_array, android.R.layout.simple_spinner_item);
        countyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpinner.setAdapter(countyAdapter);
        countySpinner.setOnItemSelectedListener(this);

        // register button on click
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
        final int lapId = id;
        final String name = etName.getText().toString();
        final String email = etEmail.getText().toString();
        final String occupation = etOccupation.getText().toString();
        final String laptop_loc = etLaptopLoc.getText().toString();
        final String address_line1 = etAddressLine1.getText().toString();
        final String address_line2 = etAddressLine2.getText().toString();
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
                        Toast.makeText(EditProfileLapActivity.this, "Editing Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfileLapActivity.this, ProfileLapActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        intent.putExtra("occupation", occupation);
                        intent.putExtra("age_range", age_range);
                        intent.putExtra("image_string", image_string);
                        intent.putExtra("laptop_loc", laptop_loc);
                        intent.putExtra("address_line1", address_line1);
                        intent.putExtra("address_line2", address_line2);
                        intent.putExtra("county", county);
                        intent.putExtra("live_campaign", live_campaign);
                        intent.putExtra("company_id", company_id);
                        EditProfileLapActivity.this.startActivity(intent);
                    } else {
                        Toast.makeText(EditProfileLapActivity.this, "Editing Failed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfileLapActivity.this, EditProfileLapActivity.class);
                        EditProfileLapActivity.this.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Add the request to the RequestQueue.
        RequestEditDetailsLap editRequest = new RequestEditDetailsLap(lapId, name, email, occupation, age_range,
                image_string, laptop_loc, address_line1, address_line2, county, responseListener);
        RequestQueue queue = Volley.newRequestQueue(EditProfileLapActivity.this);
        queue.add(editRequest);
    }


    // SPINNER METHODS
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.age_range_spinner_edit_lap:
                age_range = parent.getItemAtPosition(position).toString();
                break;
            case R.id.county_spinner_edit_lap:
                county = parent.getItemAtPosition(position).toString();
                break;
            default:
                break;
        }
    }


    // ON NOTHING SELECTED
    public void onNothingSelected(AdapterView<?> parent) {
    }


    // ON BACK BUTTON PRESSED
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(EditProfileLapActivity.this, RetrieveLapProfileDetailsActivity.class);
                intent.putExtra("lap_id", id);
                EditProfileLapActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileLapActivity.this, RetrieveLapProfileDetailsActivity.class);
        intent.putExtra("lap_id", id);
        EditProfileLapActivity.this.startActivity(intent);
    }
}
