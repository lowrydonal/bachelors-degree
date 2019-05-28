package com.lowry.lapspace3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ACTIVITY: DISPLAY DETAILS OF THE OPEN CASE THAT HAS BEEN CLICKED ON BY ADMIN
 *  LAYOUT SHOULD MIRROR COMPANY PROFILE
 */

public class DetailsOpenCaseActivity extends AppCompatActivity {

    int companyId;
    String companyName;
    int companyId2;
    String companyName2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_open_case);

        // (2) if db call is complete
        Intent intent = getIntent();
        if (intent.hasExtra("idNameString")) {
            // get data from intent
            final String idNameString = intent.getStringExtra("idNameString");
            String companyEmail = intent.getStringExtra("email");
            String logoImageString = intent.getStringExtra("imageString");
            String companyDemDesc = intent.getStringExtra("demographicDesc");
            companyId2 = intent.getIntExtra("companyId2", -1);
//            companyName2 = intent.getStringExtra("companyName2");
            final String toolbarTitle = companyName2 + " (" + companyId2 + ")";

            ImageView ivLogo = (ImageView) findViewById(R.id.iv_logo_open_case_details);
            final CollapsingToolbarLayout cTLayout = (CollapsingToolbarLayout) findViewById(R.id.ct_open_case_details);
            AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar_open_case_detail);
            TextView tvIdName = (TextView) findViewById(R.id.open_case_id_name_textview);
            TextView tvEmail = (TextView) findViewById(R.id.open_cases_email_textview);
            TextView tvDemDesc = (TextView) findViewById(R.id.case_demographic_desc_textview);
            final FloatingActionButton fabPlus = (FloatingActionButton) findViewById(R.id.floating_action_button_open_case_details);
            final FloatingActionButton fabTick = (FloatingActionButton) findViewById(R.id.floating_action_button2_open_case_details);
            final TextView fabLabel = (TextView) findViewById(R.id.activate_case_label_open_case_details);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            }
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_open_case_details);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
//        actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_close_white_18dp);

            // convert logo image string to bitmap
            byte[] byteArrayImg = Base64.decode(logoImageString, Base64.DEFAULT);
            Bitmap bitmapImg = BitmapFactory.decodeByteArray(byteArrayImg, 0, byteArrayImg.length);
            bitmapImg = getResizedBitmap(bitmapImg, 600); // resize

            // init layout
            ivLogo.setImageBitmap(bitmapImg);
            tvIdName.setText(idNameString);
            tvEmail.setText(companyEmail);
            tvDemDesc.setText(companyDemDesc);

            // app bar collapsed layout (alter title
            appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean show = true;
                int range = -1;
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (range == -1) {
                        range = appBarLayout.getTotalScrollRange();
                    }
                    if (range + verticalOffset == 0) {
                        cTLayout.setTitle(toolbarTitle);
                        show = true;
                    } else if(show) {
                        cTLayout.setTitle(" ");
                        show = false;
                    }
                }
            });

            // plus floating action button listener
            fabPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fabTick.getVisibility() == View.VISIBLE) {
                        fabTick.hide(); // hide fab
                        fabLabel.setVisibility(View.GONE);
                    }
                    else {
                        fabTick.show();
                        fabLabel.setVisibility(View.VISIBLE);
                    }
                }
            });

            // tick fab listener
            fabTick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // alter db field has_activated_case, then intent to RetrieveAvailableLapsActivity
                    new ActivateCaseTask().execute();
                }
            });
        }

        else { // (1) if db call has not yet been done
            // get the companyId that has been passed by the intent
            companyId = intent.getIntExtra("companyId", -1);
            companyName = intent.getStringExtra("companyName");

            // retrieve case details data from db
            new DbCallTask().execute();
        }
    }


    // -------------------------------------------METHODS-----------------------------------------//


    // ACTIVATE CASE IN DB TASK
    private class ActivateCaseTask extends AsyncTask<Void, Void, Void> {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        FragmentProgressBar progressBarFragment;

        @Override
        protected void onPreExecute() {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            progressBarFragment = new FragmentProgressBar();
            fragmentTransaction.replace(android.R.id.content, progressBarFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        @Override
        protected Void doInBackground(Void... params) {
            activateCaseDb();
            return null;
        }
    }

    // ACTIVATE CASE IN DB
    public void activateCaseDb() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(DetailsOpenCaseActivity.this, RetrieveAvailableLapsActivity.class);
                        intent.putExtra("companyId", companyId2);
//                        intent.putExtra("companyName", companyName2);
                        DetailsOpenCaseActivity.this.startActivity(intent);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Add the request to the RequestQueue.
        RequestActivateCase activateCaseRequest = new RequestActivateCase(companyId2, responseListener);
        RequestQueue queue = Volley.newRequestQueue(DetailsOpenCaseActivity.this);
        queue.add(activateCaseRequest);
    }



    // DB CALL ASYNC TASK
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
            fragmentTransaction.addToBackStack(null);
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
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String name = jsonResponse.getString("name");
                    String email = jsonResponse.getString("email");
                    String imageString = jsonResponse.getString("image_string");
                    String demographic_desc = jsonResponse.getString("demographic_desc");
                    String idName = companyId + ": " + name;

                    // intent back to activity from fragment after db call
                    Intent intent = new Intent(DetailsOpenCaseActivity.this, DetailsOpenCaseActivity.class);
                    intent.putExtra("companyId2", companyId);
                    intent.putExtra("idNameString", idName);
                    intent.putExtra("email", email);
                    intent.putExtra("imageString", imageString);
                    intent.putExtra("demographicDesc", demographic_desc);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestOpenCaseDetails caseDetailsRequest = new RequestOpenCaseDetails(companyId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(DetailsOpenCaseActivity.this);
        queue.add(caseDetailsRequest);
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


    // ON BACK BUTTONS PRESSED
    // fake one
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(DetailsOpenCaseActivity.this, LoginActivity.class);
                DetailsOpenCaseActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailsOpenCaseActivity.this, LoginActivity.class);
        DetailsOpenCaseActivity.this.startActivity(intent);
    }

}
