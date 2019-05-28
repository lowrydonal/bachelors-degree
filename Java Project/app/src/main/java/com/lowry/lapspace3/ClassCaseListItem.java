package com.lowry.lapspace3;

import java.io.Serializable;

/**
 * CLASS: GENERIC CASE LIST ITEM OBJECT FOR EITHER OPEN CASE, ACTIVATED CASE OR LIVE CAMPAIGN
 */

@SuppressWarnings("serial")

public class ClassCaseListItem implements Serializable{

    private int companyId;
    private String companyName;

    public ClassCaseListItem(int id, String name) {
        companyId = id;
        companyName = name;
    }

    public int getId() {
        return companyId;
    }

    public String getName() {
        return companyName;
    }
}
