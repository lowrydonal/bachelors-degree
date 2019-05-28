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
 * LIST ADAPTER: LAPTOP OWNERS
 */

public class AdapterLaptopOwnerList extends ArrayAdapter<ClassLaptopOwner> {
    public AdapterLaptopOwnerList(Context context, ArrayList<ClassLaptopOwner> laptopOwners) {
        super(context, R.layout.case_row_layout, laptopOwners);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.laptop_owner_row_layout, parent, false);

        int laptopOwnerId = getItem(position).getId();// of current case
        String name = getItem(position).getName();
        String laptop_loc = getItem(position).getLaptop_loc();
        String occupation = getItem(position).getOccupation();
        String age_range = getItem(position).getAgeRange();

        // init
        TextView idNameAgeTextview = (TextView) view.findViewById(R.id.id_name_textview);
        TextView occupationTextview = (TextView) view.findViewById(R.id.occupation_textview);
        TextView ageRangeTextview = (TextView) view.findViewById(R.id.age_range_textview);
        TextView laptopLocTextview = (TextView) view.findViewById(R.id.laptop_loc_textview);

        // fill
        String idNameString = laptopOwnerId + ": " + name;
        idNameAgeTextview.setText(idNameString);
        occupationTextview.setText(occupation);
        ageRangeTextview.setText(age_range);
        laptopLocTextview.setText(laptop_loc);

        return view;
    }
}


