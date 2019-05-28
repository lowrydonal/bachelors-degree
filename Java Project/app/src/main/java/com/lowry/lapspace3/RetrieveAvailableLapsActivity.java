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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/*
 * ACTIVITY: IN ADMIN PROFILE WHEN AN OPEN CASE LIST ITEM IS CLICKED/ACTIVATED, ADMIN IS BROUGHT HERE
 *  TO A LIST OF ALL AVAILABLE LAPTOP OWNERS. LIST ITEM CAN BE CLICKED TO ADD TO ACTIVATED CASE.
 */

public class RetrieveAvailableLapsActivity extends AppCompatActivity {

    private final String LAPTOP_OWNER_LIST_REQUEST = "https://lapspacefyp.000webhostapp.com/laptopownerlist.php";
//    final ArrayList<ClassLaptopOwner> laptopOwnersArray = new ArrayList<>();
    ClassLaptopOwnersArray laptopOwnersArray;
    boolean lapsRetrieved;
    int companyId;
//    ClassActivatedCase activatedCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_available_lap_owners);

        // get companyId passed by intent
        companyId = getIntent().getIntExtra("companyId", -1);
        lapsRetrieved = false;

        // database call: retrieve available laptop owner list items
        new DbCallTask().execute();
    }


    //------------------------------------------METHODS-------------------------------------------//


    // DATABASE CALL ASYNC TASK
    private class DbCallTask extends AsyncTask<Void, Void, Void> {
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
                    ArrayList<ClassLaptopOwner> lapsArray = new ArrayList<>();
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
                        lapsArray.add(laptopOwner);

                    }
                    laptopOwnersArray = new ClassLaptopOwnersArray(lapsArray);
                    lapsRetrieved = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        StringRequest laptopOwnerListRequest = new StringRequest(Request.Method.GET, LAPTOP_OWNER_LIST_REQUEST, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(RetrieveAvailableLapsActivity.this);
        queue.add(laptopOwnerListRequest);

        // wait until all data is retrieved from db calls before intenting to admin profile
        while (!lapsRetrieved){}
        Intent intent = new Intent(RetrieveAvailableLapsActivity.this, DisplayAvailableLapsActivity.class);
        intent.putExtra("laptopOwnersArray", laptopOwnersArray);
        intent.putExtra("companyId", companyId);
        RetrieveAvailableLapsActivity.this.startActivity(intent);

    }


//    // SEND NOTIFICATIONS TASK
//    private class SendNotificationsTask extends AsyncTask<Void, Void, Void> {
//        FragmentManager fragmentManager;
//        FragmentTransaction fragmentTransaction;
//        FragmentProgressBar progressBarFragment;
//
//        @Override
//        protected void onPreExecute() {
//            fragmentManager = getSupportFragmentManager();
//            fragmentTransaction = fragmentManager.beginTransaction();
//            progressBarFragment = new FragmentProgressBar();
//            fragmentTransaction.replace(android.R.id.content, progressBarFragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            sendNotifications();
//            return null;
//        }
//    }


//    // SEND NOTIFICATIONS (ALTER DB)
//    public void sendNotifications() {
//        final int company_id = activatedCase.getCompanyId();
//        final String laptop_owners_array = activatedCase.getLaptopOwnersArray();
//
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonResponse = new JSONObject(response);
//                    boolean success = jsonResponse.getBoolean("success");
//                    if (success) {
//                        // intent back to admin home
//                        Intent intent = new Intent(RetrieveAvailableLapsActivity.this, RequestsSentActivity.class);
//                        RetrieveAvailableLapsActivity.this.startActivity(intent);
//                    } else {
//                        Toast.makeText(RetrieveAvailableLapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(RetrieveAvailableLapsActivity.this, LoginActivity.class);
//                        RetrieveAvailableLapsActivity.this.startActivity(intent);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        // Add the request to the RequestQueue.
//        RequestSendNotifications requestSendNotifications = new RequestSendNotifications(company_id, laptop_owners_array, responseListener);
//        RequestQueue queue = Volley.newRequestQueue(RetrieveAvailableLapsActivity.this);
//        queue.add(requestSendNotifications);
//    }


    // ON BACK BUTTONS PRESSED
    // fake one
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(RetrieveAvailableLapsActivity.this, LoginActivity.class);
                RetrieveAvailableLapsActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RetrieveAvailableLapsActivity.this, LoginActivity.class);
        RetrieveAvailableLapsActivity.this.startActivity(intent);
    }
}
