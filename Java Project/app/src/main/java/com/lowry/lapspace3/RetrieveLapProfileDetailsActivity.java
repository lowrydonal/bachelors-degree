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

public class RetrieveLapProfileDetailsActivity extends AppCompatActivity {

    int lapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_lap_profile_details);

        lapId = getIntent().getIntExtra("lap_id", -1);

        // make database call
        new DbCallTask().execute();
    }


    //------------------------------------------METHODS---------------------------------------//


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
            fragmentTransaction.replace(android.R.id.content, progressBarFragment).addToBackStack(null);
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
        // response from server
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String email = jsonObject.getString("email");
                    String occupation = jsonObject.getString("occupation");
                    String age_range = jsonObject.getString("age_range");
                    String image_string = jsonObject.getString("image_string");
                    String laptop_loc = jsonObject.getString("laptop_loc");
                    String address_line1 = jsonObject.getString("address_line1");
                    String address_line2 = jsonObject.getString("address_line2");
                    String county = jsonObject.getString("county");
                    int live_campaign = jsonObject.getInt("live_campaign");
                    int company_id = jsonObject.getInt("company_id");

                    Intent intent = new Intent(RetrieveLapProfileDetailsActivity.this, ProfileLapActivity.class);
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
                    RetrieveLapProfileDetailsActivity.this.startActivity(intent);

                } catch (JSONException e) {
                    Toast.makeText(RetrieveLapProfileDetailsActivity.this, "Please sign in again", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RetrieveLapProfileDetailsActivity.this, LoginActivity.class);
                    RetrieveLapProfileDetailsActivity.this.startActivity(intent);
                    e.printStackTrace();
                }
            }
        };
        RequestRetrieveLapProfile retrieveRequest = new RequestRetrieveLapProfile(lapId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(RetrieveLapProfileDetailsActivity.this);
        queue.add(retrieveRequest);
    }
}
