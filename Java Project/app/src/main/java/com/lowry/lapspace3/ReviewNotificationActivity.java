package com.lowry.lapspace3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ReviewNotificationActivity extends AppCompatActivity {

    int companyId;
    int lapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_review_notification);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_close_white_18dp);

        // get data from intent
        Intent intent = getIntent();
        companyId = intent.getIntExtra("company_id", -1);
        lapId = intent.getIntExtra("lap_id", -1);
        final String companyName = intent.getStringExtra("company_name");
        String companyImageString = intent.getStringExtra("company_image_string");

        // init layout
        TextView tvName = (TextView) findViewById(R.id.review_notifaction_name_textview);
        Button btnAccept = (Button) findViewById(R.id.btn_accept_offer);
        Button btnDecline = (Button) findViewById(R.id.btn_decline_offer);
        ImageView ivLogo = (ImageView) findViewById(R.id.iv_review_notification);

        // convert logo image string to bitmap
        byte[] byteArrayImg = Base64.decode(companyImageString, Base64.DEFAULT);
        Bitmap bitmapImg = BitmapFactory.decodeByteArray(byteArrayImg, 0, byteArrayImg.length);
        bitmapImg = getResizedBitmap(bitmapImg, 600); // resize
        ivLogo.setImageBitmap(bitmapImg);

        // textview
        tvName.setText(companyName);

        // button click listeners
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AcceptOfferTask().execute();
            }
        });
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeclineOfferTask().execute();
            }
        });


        // app bar collapsed layout
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar_review_notification);
        final CollapsingToolbarLayout cTLayout = (CollapsingToolbarLayout) findViewById(R.id.ct_review_notification);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean show = true;
            int range = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (range == -1) {
                    range = appBarLayout.getTotalScrollRange();
                }
                if (range + verticalOffset == 0) {
                    cTLayout.setTitle(companyName);
                    show = true;
                } else if(show) {
                    cTLayout.setTitle(" ");
                    show = false;
                }
            }
        });
    }


    //------------------------------------------METHODS--------------------------------------------//


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


    // ACCEPT OFFER TASK
    private class AcceptOfferTask extends AsyncTask<Void, Void, Void> {
        // launch progress bar fragment during db call
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
            acceptOffer();
            return null;
        }
    }


    // ACCEPT OFFER
    public void acceptOffer() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(ReviewNotificationActivity.this, RetrieveLapProfileDetailsActivity.class);
                        intent.putExtra("lap_id", lapId);
                        Toast.makeText(ReviewNotificationActivity.this, "Offer Accepted", Toast.LENGTH_SHORT).show();
                        ReviewNotificationActivity.this.startActivity(intent);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Add the request to the RequestQueue.
        RequestAcceptOffer requestAcceptOffer = new RequestAcceptOffer(companyId, lapId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ReviewNotificationActivity.this);
        queue.add(requestAcceptOffer);
    }


    // DECLINE OFFER TASK
    private class DeclineOfferTask extends AsyncTask<Void, Void, Void> {
        // launch progress bar fragment during db call
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
            declineOffer();
            return null;
        }
    }


    // DECLINE OFFER
    public void declineOffer() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(ReviewNotificationActivity.this, RetrieveLapProfileDetailsActivity.class);
                        intent.putExtra("lap_id", lapId);
                        Toast.makeText(ReviewNotificationActivity.this, "Offer Declined", Toast.LENGTH_SHORT).show();
                        ReviewNotificationActivity.this.startActivity(intent);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Add the request to the RequestQueue.
        RequestDeclineOffer requestDeclineOffer = new RequestDeclineOffer(lapId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ReviewNotificationActivity.this);
        queue.add(requestDeclineOffer);
    }


    // ON BACK BUTTON PRESSED
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ReviewNotificationActivity.this, RetrieveLapProfileDetailsActivity.class);
                intent.putExtra("lap_id", lapId);
                ReviewNotificationActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReviewNotificationActivity.this, RetrieveLapProfileDetailsActivity.class);
        intent.putExtra("lap_id", lapId);
        ReviewNotificationActivity.this.startActivity(intent);
    }
}

