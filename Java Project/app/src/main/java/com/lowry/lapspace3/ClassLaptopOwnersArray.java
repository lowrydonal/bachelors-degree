package com.lowry.lapspace3;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * CLASS: SERIALIZABLE ARRAY OF LAPTOP OWNER OBJECTS USED IN ADMIN ACTIVITIES
 */

@SuppressWarnings("serial")

public class ClassLaptopOwnersArray implements Serializable {
    private ArrayList<ClassLaptopOwner> laptopOwnerArray;

    public ClassLaptopOwnersArray(ArrayList<ClassLaptopOwner> laptopOwnerArray) {
        this.laptopOwnerArray = laptopOwnerArray;
    }

    public ArrayList<ClassLaptopOwner> getLaptopOwnerArray() {
        return laptopOwnerArray;
    }
}
