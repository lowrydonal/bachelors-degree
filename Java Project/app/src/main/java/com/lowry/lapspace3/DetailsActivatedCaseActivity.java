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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * ACTIVITY: DISPLAY DETAILS OF THE ACTIVATED CASE THAT HAS BEEN CLICKED ON BY ADMIN
 */

public class DetailsActivatedCaseActivity extends AppCompatActivity {

    final ArrayList<ClassLaptopOwner> laptopOwnersArray = new ArrayList<>();
    int companyId;
    String companyName;
    int companyId2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_activated_case);

        // (2) if db call is complete
        Intent intent = getIntent();
        if (intent.hasExtra("lapArray")) {
            // get data from intent
            ClassLaptopOwnersArray lapOwnerArray2 = (ClassLaptopOwnersArray) intent.getSerializableExtra("lapArray");
            final ArrayList<ClassLaptopOwner> laptopOwnersArray2 = lapOwnerArray2.getLaptopOwnerArray();
            companyId2 = intent.getIntExtra("companyId2", -1);
            String companyName2 = intent.getStringExtra("companyName2");
            String toolbarTitle = companyName2 + " (" + companyId2 + ")";

            FloatingActionButton fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus_activated_case_details);
            final FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add_laps);
            final FloatingActionButton fabCon = (FloatingActionButton) findViewById(R.id.fab_convert);
            final TextView addLapsLabel = (TextView) findViewById(R.id.add_laps_label);
            final TextView conLabel = (TextView) findViewById(R.id.convert_label);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_act_details);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(toolbarTitle);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_close_white_18dp);

            // plus floating action button listener
            fabPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fabAdd.getVisibility() == View.VISIBLE) {
                        fabAdd.hide(); // hide fab
                        addLapsLabel.setVisibility(View.GONE);
                        fabCon.hide();
                        conLabel.setVisibility(View.GONE);
                    }
                    else {
                        fabAdd.show(); // show fab
                        addLapsLabel.setVisibility(View.VISIBLE);
                        fabCon.show(); // show fab
                        conLabel.setVisibility(View.VISIBLE);
                    }
                }
            });

            // convert to live campaignn fab listener
            fabCon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ConvertToLiveCampaignTask().execute();
                }
            });

            // init listview (items not clickable)
            ListAdapter adapter = new AdapterLaptopOwnerList(this, laptopOwnersArray2);
            ListView listview = (ListView) findViewById(R.id.act_case_laps_list);
            listview.setAdapter(adapter);
        }

        else { // (1) if db call has not yet been done
            // get the companyId that has been passed by the intent
            companyId = intent.getIntExtra("companyId", -1);
            companyName = intent.getStringExtra("companyName");
            // retrieve case details data from db (list of associated laps)
            new DbCallTask().execute();
        }
    }


    // -------------------------------------------METHODS-----------------------------------------//


    // CONVERT TO LIVE CAMPAIGN TASK
    private class ConvertToLiveCampaignTask extends AsyncTask<Void, Void, Void> {
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
            convertToLiveCampaign();
            return null;
        }
    }


    // CONVERT TO LIVE CAMPAIGN
    public void convertToLiveCampaign() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(DetailsActivatedCaseActivity.this, ConvertedToLiveActivity.class);
                        DetailsActivatedCaseActivity.this.startActivity(intent);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Add the request to the RequestQueue.
        RequestConvertToLiveCampaign convertRequest = new RequestConvertToLiveCampaign(companyId2, responseListener);
        RequestQueue queue = Volley.newRequestQueue(DetailsActivatedCaseActivity.this);
        queue.add(convertRequest);
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

    // DATABASE CALL
    public void dbCall() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    // add all elements of jsonArray to our laptop owner array
                    for (int i = 0; i < jsonArray.length(); i ++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        String email = jsonObject.getString("email");
                        String laptop_loc = jsonObject.getString("laptop_loc");
                        String occupation = jsonObject.getString("occupation");
                        String age_range = jsonObject.getString("age_range");
                        ClassLaptopOwner laptopOwner = new ClassLaptopOwner(id, name, email, laptop_loc, occupation, age_range);
                        laptopOwnersArray.add(laptopOwner);

                    }
                    ClassLaptopOwnersArray lapArray = new ClassLaptopOwnersArray(laptopOwnersArray);

                    // send to new activity to display listview of available laptop owners
                    Intent intent = new Intent(DetailsActivatedCaseActivity.this, DetailsActivatedCaseActivity.class);
                    intent.putExtra("lapArray", lapArray);
                    intent.putExtra("companyId2", companyId);
                    intent.putExtra("companyName2", companyName);
                    DetailsActivatedCaseActivity.this.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestActLapList laptopOwnerListRequest = new RequestActLapList(companyId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(DetailsActivatedCaseActivity.this);
        queue.add(laptopOwnerListRequest);
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
                Intent intent = new Intent(DetailsActivatedCaseActivity.this, LoginActivity.class);
                DetailsActivatedCaseActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailsActivatedCaseActivity.this, LoginActivity.class);
        DetailsActivatedCaseActivity.this.startActivity(intent);
    }
}
