package com.lowry.lapspace3;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * CLASS: GENERIC CASE LIST ITEM ARRAY OBJECT FOR EITHER OPEN CASE, ACTIVATED CASE OR LIVE CAMPAIGN
 *  SERIALIZABLE SO THAT THEY CAN BE PASSED VIA INTENT
 */

@SuppressWarnings("serial")

public class CaseListItemArray implements Serializable {
    private ArrayList<ClassCaseListItem> caseListItemsArray;

    public CaseListItemArray(ArrayList<ClassCaseListItem> caseListItemsArray) {
        this.caseListItemsArray = caseListItemsArray;
    }

    public ArrayList<ClassCaseListItem> getCaseListItemsArray() {
        return caseListItemsArray;
    }
}
