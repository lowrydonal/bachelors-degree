package com.lowry.lapspace3;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SuccessVerifyActivity extends AppCompatActivity {

    int lapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_verify);

        lapId = getIntent().getIntExtra("lap_id", -1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_succ_ver);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_close_white_18dp);
    }


    //-----------------------------------------METHODS-----------------------------------------//


    // ON BACK BUTTON PRESSED
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SuccessVerifyActivity.this, RetrieveLapProfileDetailsActivity.class);
                intent.putExtra("lap_id", lapId);
                SuccessVerifyActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SuccessVerifyActivity.this, RetrieveLapProfileDetailsActivity.class);
        intent.putExtra("lap_id", lapId);
        SuccessVerifyActivity.this.startActivity(intent);
    }
}
