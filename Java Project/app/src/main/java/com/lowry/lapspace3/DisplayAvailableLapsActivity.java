package com.lowry.lapspace3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * ACTIVITY: AVAILABLE LAPTOP OWNERS RETRIEVED FROM RetrieveAvailableLapsActivity.
 *  THIS ACTIVITY WILL DISPLAY SAID LIST
 */

public class DisplayAvailableLapsActivity extends AppCompatActivity {

    ClassActivatedCase activatedCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_available_laps);

        // get params passed from intent
        Intent intent = getIntent();
        int companyId = intent.getIntExtra("companyId", -1);
//        String companyName = intent.getStringExtra("companyName");
//        String toolbarTitle = companyName + " (" + companyId + ")";
        ClassLaptopOwnersArray lapOwner = (ClassLaptopOwnersArray) intent.getSerializableExtra("laptopOwnersArray");
        final ArrayList<ClassLaptopOwner> laptopOwnersArray = lapOwner.getLaptopOwnerArray();

        // init layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_lap_list);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Laptop Owners to Case");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_close_white_18dp);

        // create activated case to add available laptop owners to clicking on list items
        activatedCase = new ClassActivatedCase(companyId);
        FloatingActionButton fabPlus = (FloatingActionButton) findViewById(R.id.fab_send_requests);
        final FloatingActionButton fabConfirmSend = (FloatingActionButton) findViewById(R.id.fab_confirm_send);
        final TextView fabLabel = (TextView) findViewById(R.id.send_requests_label);

        // init listview
        ListAdapter adapter = new AdapterLaptopOwnerList(this, laptopOwnersArray);
        ListView listview = (ListView) findViewById(R.id.laptop_owners_listview);
        listview.setAdapter(adapter);

        // add laptop owner list items to activated case when clicked on
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassLaptopOwner laptopOwnerToAdd = laptopOwnersArray.get(position);
                activatedCase.addLaptopOwner(laptopOwnerToAdd.getId());

                String toastName = laptopOwnerToAdd.getName();
                String toastString = toastName + " added to case";
                Toast.makeText(DisplayAvailableLapsActivity.this, toastString, Toast.LENGTH_LONG).show();
            }
        });

        // plus fab listener
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fabConfirmSend.getVisibility() == View.VISIBLE) {
                    fabConfirmSend.hide(); // hide fab
                    fabLabel.setVisibility(View.GONE);
                }
                else {
                    fabConfirmSend.show();
                    fabLabel.setVisibility(View.VISIBLE);
                }
            }
        });

        // send request fab listener
        fabConfirmSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send notifications = advertiser db field request_id assigned company_id of activated case object
                new SendNotificationsTask().execute();
            }
        });
    }


    //-------------------------------------------METHODS-----------------------------------------//


    // SEND NOTIFICATIONS TASK
    private class SendNotificationsTask extends AsyncTask<Void, Void, Void> {
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
            sendNotifications();
            return null;
        }
    }


    // SEND NOTIFICATIONS (ALTER DB)
    public void sendNotifications() {
        final int company_id = activatedCase.getCompanyId();
        final String laptop_owners_array = activatedCase.getLaptopOwnersArray();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        // intent back to admin home
                        Intent intent = new Intent(DisplayAvailableLapsActivity.this, RequestsSentActivity.class);
                        DisplayAvailableLapsActivity.this.startActivity(intent);
                    } else {
                        Toast.makeText(DisplayAvailableLapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DisplayAvailableLapsActivity.this, LoginActivity.class);
                        DisplayAvailableLapsActivity.this.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // Add the request to the RequestQueue.
        RequestSendNotifications requestSendNotifications = new RequestSendNotifications(company_id, laptop_owners_array, responseListener);
        RequestQueue queue = Volley.newRequestQueue(DisplayAvailableLapsActivity.this);
        queue.add(requestSendNotifications);
    }


    // ON BACK BUTTONS PRESSED
    // fake one
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(DisplayAvailableLapsActivity.this, LoginActivity.class);
                DisplayAvailableLapsActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DisplayAvailableLapsActivity.this, LoginActivity.class);
        DisplayAvailableLapsActivity.this.startActivity(intent);
    }
}
