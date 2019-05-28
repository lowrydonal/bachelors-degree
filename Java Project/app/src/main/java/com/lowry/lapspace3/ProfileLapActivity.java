package com.lowry.lapspace3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ACTIVITY: LAPTOP OWNER PROFILE
 */

public class ProfileLapActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_lap);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("id", -1);
        final String name = intent.getStringExtra("name");
        final String email = intent.getStringExtra("email");
        final String occupation = intent.getStringExtra("occupation");
        final String age_range = intent.getStringExtra("age_range");
        final String image_string = intent.getStringExtra("image_string");
        final String laptop_loc = intent.getStringExtra("laptop_loc");
        final String address_line1 = intent.getStringExtra("address_line1");
        final String address_line2 = intent.getStringExtra("address_line2");
        final String county = intent.getStringExtra("county");
        final int live_campaign = intent.getIntExtra("live_campaign", -1);
        final int company_id = intent.getIntExtra("company_id", -1);

        // init layout
        final CollapsingToolbarLayout cTLayout = (CollapsingToolbarLayout) findViewById(R.id.ct_lap_profile);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_lap_profile);
        TextView tvName = (TextView) findViewById(R.id.tv_name);
        TextView tvOccupation = (TextView) findViewById(R.id.tv_occupation);
        TextView tvAgeRange = (TextView) findViewById(R.id.tv_age_range);
        ImageView ivLap = (ImageView) findViewById(R.id.iv_lap_profile);
        TextView tvLaptopLoc = (TextView) findViewById(R.id.tv_laptop_loc);
        TextView tvAddress = (TextView) findViewById(R.id.tv_address);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_advertiser_profile);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_drawer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        View navHeader = navigationView.getHeaderView(0);
        ImageView navIv = (ImageView) navHeader.findViewById(R.id.nav_iv_lap);
        TextView navTv = (TextView) navHeader.findViewById(R.id.nav_tv_lap);
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar_lap_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_lap_profile);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);

        // init layout
        tvName.setText(name);
        tvOccupation.setText(occupation);
        tvAgeRange.setText(age_range);
        tvLaptopLoc.setText(laptop_loc);
        // convert logo image string to bitmap
        byte[] byteArrayImg = Base64.decode(image_string, Base64.DEFAULT);
        Bitmap bitmapImg = BitmapFactory.decodeByteArray(byteArrayImg, 0, byteArrayImg.length);
        bitmapImg = getResizedBitmap(bitmapImg, 600); // resize
        ivLap.setImageBitmap(bitmapImg);
        navTv.setText(name);
        // address
        String addressString = address_line1;
        if (address_line2 != null) {
            addressString += "\n" + address_line2;
        }
        addressString += county;
        tvAddress.setText(addressString);

        // navigation drawer button click listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        if (menuItem.getTitle() == getResources().getString(R.string.notifications_drawer_btn)) {
                            Intent intent = new Intent(ProfileLapActivity.this, RetrieveNotificationDetails.class);
                            intent.putExtra("lap_id", id);
//                            intent.putExtra("company_id", company_id);
                            ProfileLapActivity.this.startActivity(intent);
                        }
                        else if (menuItem.getTitle() == getResources().getString(R.string.verify_drawer_btn)) {
                            // go to GetImagesActivity activity
                            Intent intent = new Intent(ProfileLapActivity.this, GetImagesActivity.class);
                            intent.putExtra("company_id", company_id);
                            intent.putExtra("lap_id", id);
                            ProfileLapActivity.this.startActivity(intent);
                        }
                        else if (menuItem.getTitle() == getResources().getString(R.string.edit_profile_drawer_btn)) {
                            // go to EditProfileLapActivity
                            Intent intent = new Intent(ProfileLapActivity.this, EditProfileLapActivity.class);
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
                            ProfileLapActivity.this.startActivity(intent);
                        }
                        else if (menuItem.getTitle() == getResources().getString(R.string.settings_drawer_btn)) {
                            // TODO
                        }
                        return true;
                    }
                });

        // app bar collapsed layout
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean show = true;
            int range = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (range == -1) {
                    range = appBarLayout.getTotalScrollRange();
                }
                if (range + verticalOffset == 0) {
                    cTLayout.setTitle(name);
                    cTLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
                    show = true;
                } else if(show) {
                    cTLayout.setTitle(" ");
                    show = false;
                }
            }
        });

        // plus floating action button listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileLapActivity.this, EditProfileLapActivity.class);
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
                ProfileLapActivity.this.startActivity(intent);
            }
        });
    } // end onCreate


    //----------------------------------------METHODS--------------------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    // ON BACK BUTTON PRESSED
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileLapActivity.this, LoginActivity.class);
        ProfileLapActivity.this.startActivity(intent);
    }
}
