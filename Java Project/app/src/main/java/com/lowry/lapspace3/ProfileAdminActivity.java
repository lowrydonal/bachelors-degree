package com.lowry.lapspace3;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import java.util.ArrayList;

/*
 * ACTIVITY: ADMIN PROFILE DISPLAYS A 3 TABS: OPEN CASES, ACTIVATED CASES AND LIVE CAMPAIGNS
 */

public class ProfileAdminActivity extends AppCompatActivity {

    // arrays to be inflated into listviews
    private ArrayList<ClassCaseListItem> openCasesListItems;
    private ArrayList<ClassCaseListItem> activatedCasesListItems;
    private ArrayList<ClassCaseListItem> liveCampaignListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        // get arrays to be inflated from intent
        Intent intent = getIntent();
        CaseListItemArray openCasesArray = (CaseListItemArray) intent.getSerializableExtra("openCaseListItemArray");
        CaseListItemArray activatedCasesArray = (CaseListItemArray) intent.getSerializableExtra("activatedCaseListItemArray");
        CaseListItemArray liveCampaignsArray = (CaseListItemArray) intent.getSerializableExtra("liveCampaignListItemArray");
        // error handle if lists are empty
//        if (openCasesArray.getCaseListItemsArray() == null) {
//            openCasesListItems = new ArrayList<>();
//        } else {
//            openCasesListItems = openCasesArray.getCaseListItemsArray();
//        }
        openCasesListItems = openCasesArray.getCaseListItemsArray();
        activatedCasesListItems = activatedCasesArray.getCaseListItemsArray();
        liveCampaignListItems = liveCampaignsArray.getCaseListItemsArray();

        // init tab layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_admin_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.admin_profile_toolbar_title));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_admin_profile);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.open_cases_tab)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.activated_cases_tab)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.live_campaigns_tab)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        // init viewpager
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager_admin_profile);
        final AdapterViewPager adapter = new AdapterViewPager(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    //-----------------------------------------METHODS---------------------------------------------//


    // GETTERS USED BY TAB FRAGMENTS
    public ArrayList<ClassCaseListItem> getOpenCasesArray() {
        return openCasesListItems;
    }
    public ArrayList<ClassCaseListItem> getActivatedCasesArray() {
        return activatedCasesListItems;
    }
    public ArrayList<ClassCaseListItem> getLiveCampaignsArray() {
        return liveCampaignListItems;
    }


    // ON BACK BUTTON PRESSED
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileAdminActivity.this, LoginActivity.class);
        ProfileAdminActivity.this.startActivity(intent);
    }


}

