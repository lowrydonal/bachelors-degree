package com.lowry.lapspace3;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * CLASS: ACTIVATED CASE THAT LAPTOP OWNERS CAN BE ADDED TO
 */

@SuppressWarnings("serial")

public class ClassActivatedCase implements Serializable {

    private int companyId; // company linked to activated case
//    ArrayList<ClassLaptopOwner> laptopOwnersArray; // laptop owners linked to activated case
    String laptopOwnersArray; // will use comma delimeter to split up in php

    public ClassActivatedCase(int id) {
        companyId = id;
//        laptopOwnersArray = new ArrayList<>();
        laptopOwnersArray = null;
    }

//    // ADD LAPTOP OWNER
//    public void addLaptopOwner(ClassLaptopOwner laptopOwner) {
//        laptopOwnersArray.add(laptopOwner);
//    }
//
//    // GETTERS
//    public ArrayList<ClassLaptopOwner> getLaptopOwnersArray() {
//        return laptopOwnersArray;
//    }

    // ADD LAPTOP OWNER
    public void addLaptopOwner(int laptopOwnerId) {
        if (laptopOwnersArray == null) {
            laptopOwnersArray = Integer.toString(laptopOwnerId);
        }
        else { // if not the first addition to array
            String comma = " ";
            laptopOwnersArray = laptopOwnersArray.concat(comma);
            laptopOwnersArray = laptopOwnersArray.concat(Integer.toString(laptopOwnerId));
        }
    }

    // GETTERS
    public String getLaptopOwnersArray() {
        return laptopOwnersArray;
    }

    public int getCompanyId() {
        return companyId;
    }
}
