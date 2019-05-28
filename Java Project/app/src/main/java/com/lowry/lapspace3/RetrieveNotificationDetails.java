package com.lowry.lapspace3;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RetrieveNotificationDetails extends AppCompatActivity {

//    int companyId; // does not need to be passed, lapOwner has request_id field
    int lapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_notification_details);

        Intent intent = getIntent();
//        companyId = intent.getIntExtra("companyId", -1);
        lapId = intent.getIntExtra("lap_id", -1);

        // make database call
        new DbCallTask().execute();
    }


    //-------------------------------------------METHODS----------------------------------------//


    // DB CALL AND START PROGRESS FRAGMENT ASYNC TASK
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


    // DB CALL
    public void dbCall() {
        final ClassNotificationDetails[] notificationDetails = new ClassNotificationDetails[1];
        // response from server
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject jsonObject = jsonResponse.getJSONObject(0);
                    int request_id = jsonObject.getInt("request_id");
                    // if no notifications
                    if (request_id == -1) {
                        Intent intent = new Intent(RetrieveNotificationDetails.this, NoNotificationsActivity.class);
                        intent.putExtra("lap_id", lapId);
                        RetrieveNotificationDetails.this.startActivity(intent);
                    }
                    else { // if a live request
                        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response2) {
                                try {
                                    JSONArray jsonResponse2 = new JSONArray(response2);
                                    JSONObject jsonObject2 = jsonResponse2.getJSONObject(0);

                                    int company_id = jsonObject2.getInt("id");
                                    String company_name = jsonObject2.getString("name");
                                    String company_image_string = jsonObject2.getString("image_string");

                                    Intent intent = new Intent(RetrieveNotificationDetails.this, ReviewNotificationActivity.class);
                                    intent.putExtra("company_name", company_name);
                                    intent.putExtra("company_image_string", company_image_string);
                                    intent.putExtra("company_id", company_id);
                                    intent.putExtra("lap_id", lapId);
                                    RetrieveNotificationDetails.this.startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(RetrieveNotificationDetails.this, "Could not retrieve request details", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RetrieveNotificationDetails.this, ProfileLapActivity.class); // will state be saved?
                                    RetrieveNotificationDetails.this.startActivity(intent);
                                }
                            }
                        };
                        RequestNotificationDetails requestNotificationDetails = new RequestNotificationDetails(request_id, responseListener2);
                        RequestQueue queue = Volley.newRequestQueue(RetrieveNotificationDetails.this);
                        queue.add(requestNotificationDetails);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestNotificationStatus requestNotificationStatus = new RequestNotificationStatus(lapId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(RetrieveNotificationDetails.this);
        queue.add(requestNotificationStatus);
    }
}
