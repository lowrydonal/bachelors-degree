package com.lowry.lapspace3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lowry on 14/02/2018.
 */

public class AdapterCaseList extends ArrayAdapter<ClassCaseListItem> {
    public AdapterCaseList(Context context, ArrayList<ClassCaseListItem> cases) {
        super(context, R.layout.case_row_layout, cases);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.case_row_layout, parent, false);

        int companyId = getItem(position).getId();// of current case
        String companyName = getItem(position).getName();

        TextView caseRowLayoutTvId = (TextView) view.findViewById(R.id.case_row_layout_tv_id);
        TextView caseRowLayoutTvName = (TextView) view.findViewById(R.id.case_row_layout_tv_name);
        caseRowLayoutTvName.setText(companyName);
        String idString = "Company ID Number: " + companyId;
        caseRowLayoutTvId.setText(idString);

        return view;
    }
}


