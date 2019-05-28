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
 * FRAGMENT: LIST OF ACTIVATED CASES (PART OF ADMIN PROFILE)
 */

public class FragmentListActivatedCases extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_activated_cases, container, false);
        ProfileAdminActivity activity = (ProfileAdminActivity) getActivity();
        final ArrayList<ClassCaseListItem> activatedCasesListItems = activity.getActivatedCasesArray();

        // inflate listview
        ListAdapter adapter = new AdapterCaseList(activity, activatedCasesListItems);
        ListView listview = (ListView) view.findViewById(R.id.activated_cases_listview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int companyId = activatedCasesListItems.get(position).getId();
                String companyName = activatedCasesListItems.get(position).getName();
                Intent intent = new Intent(getActivity(), DetailsActivatedCaseActivity.class);
                intent.putExtra("companyName", companyName);
                intent.putExtra("companyId", companyId);
                startActivity(intent);
            }
        });
        return view;
    }
}
