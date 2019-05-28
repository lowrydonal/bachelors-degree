package com.lowry.lapspace3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ACTIVITY: LOGIN
 */

public class LoginActivity extends AppCompatActivity {

    boolean advertiser = false;
    boolean company = false;
    EditText etLoginEmail;
    EditText etLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init layout
        etLoginEmail = (EditText) findViewById(R.id.et_login_email);
        etLoginPassword = (EditText) findViewById(R.id.et_login_password);
        final Button btnLogin = (Button) findViewById(R.id.btn_login);
        final TextView tvRegLinkAd = (TextView) findViewById(R.id.tv_reg_link_ad);
        final TextView tvRegLinkCo = (TextView) findViewById(R.id.tv_reg_link_co);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }


        // if "Login" button pressed
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard when login button pressed
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // make database call
                new DbCallTask().execute();
            }
        });


        // if "Register as ClassLaptopOwner" button pressed
        tvRegLinkAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegisterLapActivity.class);
            LoginActivity.this.startActivity(intent);
            }
        });


        // if "Register as Company" button pressed [intent to that page]
        tvRegLinkCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegisterCoActivity.class);
            LoginActivity.this.startActivity(intent);
            }
        });

    }


    //------------------------------------------MEHTODS--------------------------------------------//


    // RADIO BUTTON LISTENER
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_ad: {
                if (checked) {
                    advertiser = true;
                    company = false;
                    break;
                }
            }
            case R.id.radio_co: {
                if (checked) {
                    company = true;
                    advertiser = false;
                    break;
                }
            }
        }
    }


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
        final String email = etLoginEmail.getText().toString();
        final String password = etLoginPassword.getText().toString();

        if (advertiser) {
            // response from server
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            int is_admin = jsonResponse.getInt("is_admin");
                            if (is_admin > 0) { // if admin send to admin profile
                                Intent intent = new Intent(LoginActivity.this, RetrieveAdminProfileDataActivity.class);
                                LoginActivity.this.startActivity(intent);
                            }
                            else { // if not admin
                                int id = jsonResponse.getInt("id");
                                String name = jsonResponse.getString("name");
                                String email = jsonResponse.getString("name");
                                String occupation = jsonResponse.getString("occupation");
                                String age_range = jsonResponse.getString("age_range");
                                String image_string = jsonResponse.getString("image_string");
                                String laptop_loc = jsonResponse.getString("laptop_loc");
                                String address_line1 = jsonResponse.getString("address_line1");
                                String address_line2 = jsonResponse.getString("address_line2");
                                String county = jsonResponse.getString("county");
                                int live_campaign = jsonResponse.getInt("live_campaign");
                                int company_id = jsonResponse.getInt("company_id");

                                Intent intent = new Intent(LoginActivity.this, ProfileLapActivity.class);
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
                                LoginActivity.this.startActivity(intent);
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            LoginActivity.this.startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestLoginLap loginRequest = new RequestLoginLap(email, password, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        }
        if (company){
            // response from server
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            int is_admin = jsonResponse.getInt("is_admin");
                            if (is_admin > 0) { // if admin
                                Intent intent = new Intent(LoginActivity.this, RetrieveAdminProfileDataActivity.class);
                                LoginActivity.this.startActivity(intent);
                            }

                            else { // if not admin
                                String name = jsonResponse.getString("name");
                                String image_string = jsonResponse.getString("image_string");
                                String demographic_desc = jsonResponse.getString("demographic_desc");
                                int id = jsonResponse.getInt("id");

                                Intent intent = new Intent(LoginActivity.this, ProfileCoActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("image_string", image_string);
                                intent.putExtra("demographic_desc", demographic_desc);
                                LoginActivity.this.startActivity(intent);
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            LoginActivity.this.startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestLoginCo loginRequestCo = new RequestLoginCo(email, password, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequestCo);
        }
    }
}
