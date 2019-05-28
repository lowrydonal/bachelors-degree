package com.lowry.lapspace3;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
 * ACTIVITY: RETRIEVES DATA WHICH WILL BE DISPLAYED IN ADMIN PROFILE
 *  PASS 3 ARRAYS TO ADMIN PROFILE: OPEN CASES, ACTIVATED CASES AND LIVE CAMPAIGNS
 */

public class RetrieveAdminProfileDataActivity extends AppCompatActivity {

    private final String OPEN_CASES_LIST_REQUEST = "https://lapspacefyp.000webhostapp.com/opencaselist.php";
    private final String ACTIVATED_CASES_LIST_REQUEST = "https://lapspacefyp.000webhostapp.com/activatedcaselist.php";
    private final String LIVE_CAMPAIGNS_LIST_REQUEST = "https://lapspacefyp.000webhostapp.com/livecampaignlist.php";

    // arrays to be inflated into listviews in admin profile
    CaseListItemArray openCaseListItemArray;
    CaseListItemArray activatedCaseListItemArray;
    CaseListItemArray liveCampaignListItemArray;
    boolean openCasesRetrieved;
    boolean activatedCasesRetrieved;
    boolean liveCampaignsRetrieved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_admin_profile_data);

        openCasesRetrieved = false;
        activatedCasesRetrieved = false;
        liveCampaignsRetrieved = false;

        // retrieve listview items in array form and pass to admin profile
        new dbCallsTask().execute();
    }


    //--------------------------------------------METHODS------------------------------------------//


    // DB CALL ASYNC TASK
    private class dbCallsTask extends AsyncTask<Void, Void, Void> {
        // launch progress bar fragment during db call
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        FragmentSplashScreen fragmentSplashScreen;

        @Override
        protected void onPreExecute() {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentSplashScreen = new FragmentSplashScreen();
            fragmentTransaction.replace(android.R.id.content, fragmentSplashScreen);
            fragmentTransaction.commit();
        }
        @Override
        protected Void doInBackground(Void... params) {
            dbCalls();
            return null;
        }
    }


    // DB CALLS
    public void dbCalls() {
        // open cases
        Response.Listener<String> responseListener1 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<ClassCaseListItem> openCasesArray = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    // add all elements of jsonArray to our case array
                    for (int i = 0; i < jsonArray.length(); i ++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        ClassCaseListItem caseListItem = new ClassCaseListItem(id, name);
                        openCasesArray.add(caseListItem);
                    }
                    openCaseListItemArray = new CaseListItemArray(openCasesArray);
                    openCasesRetrieved = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        StringRequest openCaseListRequest = new StringRequest(Request.Method.GET, OPEN_CASES_LIST_REQUEST, responseListener1, null);
        RequestQueue queue = Volley.newRequestQueue(RetrieveAdminProfileDataActivity.this);
        queue.add(openCaseListRequest);

        // activated cases
        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<ClassCaseListItem> activatedCasesArray = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    // add all elements of jsonArray to our case array
                    for (int i = 0; i < jsonArray.length(); i ++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        ClassCaseListItem caseListItem = new ClassCaseListItem(id, name);
                        activatedCasesArray.add(caseListItem);
                    }
                    activatedCaseListItemArray = new CaseListItemArray(activatedCasesArray);
                    activatedCasesRetrieved = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        StringRequest activatedCaseListRequest = new StringRequest(Request.Method.GET, ACTIVATED_CASES_LIST_REQUEST, responseListener2, null);
        queue.add(activatedCaseListRequest);

        // live campaigns
        Response.Listener<String> responseListener3 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<ClassCaseListItem> liveCampaignsArray = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    // add all elements of jsonArray to our case array
                    for (int i = 0; i < jsonArray.length(); i ++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        ClassCaseListItem caseListItem = new ClassCaseListItem(id, name);
                        liveCampaignsArray.add(caseListItem);
                    }
                    liveCampaignListItemArray = new CaseListItemArray(liveCampaignsArray);
                    liveCampaignsRetrieved = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        StringRequest liveCampaignsListRequest = new StringRequest(Request.Method.GET, LIVE_CAMPAIGNS_LIST_REQUEST, responseListener3, null);
        queue.add(liveCampaignsListRequest);
        // wait until all data is retrieved from db calls before intenting to admin profile
        while (!openCasesRetrieved || !activatedCasesRetrieved || !liveCampaignsRetrieved){}

        Intent intent = new Intent(RetrieveAdminProfileDataActivity.this, ProfileAdminActivity.class);
        intent.putExtra("openCaseListItemArray", openCaseListItemArray);
        intent.putExtra("activatedCaseListItemArray", activatedCaseListItemArray);
        intent.putExtra("liveCampaignListItemArray", liveCampaignListItemArray);
        RetrieveAdminProfileDataActivity.this.startActivity(intent);
    }

//    @Override
//    public void onResume() {
////        super.onResume();
////        setContentView(R.layout.activity_retrieve_admin_profile_data);
////
////        openCasesRetrieved = false;
////        activatedCasesRetrieved = false;
////        liveCampaignsRetrieved = false;
////
////        // retrieve listview items in array form and pass to admin profile
////        new dbCallsTask().execute();
//    }
}