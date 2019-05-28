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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ACTIVITY: COMPANY OWNER PROFILE
 */

public class ProfileCoActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_co);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("id", -1);
        final String name = intent.getStringExtra("name");
        final String email = intent.getStringExtra("email");
        final String image_string = intent.getStringExtra("image_string");
        final String demographic_desc = intent.getStringExtra("demographic_desc");

        // init layout
        final CollapsingToolbarLayout cTLayout = (CollapsingToolbarLayout) findViewById(R.id.ct_co_profile);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_co_profile);
        TextView tvName = (TextView) findViewById(R.id.tv_name_co_profile);
        TextView tvEmail = (TextView) findViewById(R.id.tv_email_co_profile);
        TextView tvDemographicDesc = (TextView) findViewById(R.id.tv_dem_desc_co_profile);
        ImageView ivCo = (ImageView) findViewById(R.id.iv_co_profile);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_co_profile);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_drawer_co_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        View navHeader = navigationView.getHeaderView(0);
        ImageView navIv = (ImageView) navHeader.findViewById(R.id.nav_iv_co);
        TextView navTv = (TextView) navHeader.findViewById(R.id.nav_tv_co);
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar_co_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_co_profile);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);

        // init layout
        tvName.setText(name);
        tvEmail.setText(email);
        tvDemographicDesc.setText(demographic_desc);
        // convert logo image string to bitmap
        byte[] byteArrayImg = Base64.decode(image_string, Base64.DEFAULT);
        Bitmap bitmapImg = BitmapFactory.decodeByteArray(byteArrayImg, 0, byteArrayImg.length);
        bitmapImg = getResizedBitmap(bitmapImg, 600); // resize
        ivCo.setImageBitmap(bitmapImg);
        navTv.setText(name);

        // navigation drawer button click listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        if (menuItem.getTitle() == getResources().getString(R.string.make_payment_nav_btn)) {
                            // TODO
                        }
                        else if (menuItem.getTitle() == getResources().getString(R.string.edit_profile_drawer_btn)) {
                            // go to EditProfileCoActivity
                            Intent intent = new Intent(ProfileCoActivity.this, EditProfileCoActivity.class);
                            intent.putExtra("id", id);
                            ProfileCoActivity.this.startActivity(intent);
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
                Intent intent = new Intent(ProfileCoActivity.this, EditProfileCoActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("demographic_desc", demographic_desc);
                ProfileCoActivity.this.startActivity(intent);
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


    // ON BACK BUTTON PRESSED
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileCoActivity.this, LoginActivity.class);
        ProfileCoActivity.this.startActivity(intent);
    }


}
