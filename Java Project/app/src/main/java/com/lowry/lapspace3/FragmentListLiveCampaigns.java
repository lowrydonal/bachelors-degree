package com.lowry.lapspace3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * FRAGMENT: LIST OF LIVE CAMPAIGNS (PART OF ADMIN PROFILE)
 */

public class FragmentListLiveCampaigns extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_live_campaign, container, false);
        ProfileAdminActivity activity = (ProfileAdminActivity) getActivity();
        final ArrayList<ClassCaseListItem> liveCampaignListItems = activity.getLiveCampaignsArray();

        // inflate listview
        ListAdapter adapter = new AdapterCaseList(activity, liveCampaignListItems);
        ListView listview = (ListView) view.findViewById(R.id.live_campaigns_listview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int companyId = liveCampaignListItems.get(position).getId();
                Intent intent = new Intent(getActivity(), DetailsLiveCampaignActivity.class);
                intent.putExtra("companyId", companyId);
                startActivity(intent);
            }
        });
        return view;
    }
}
