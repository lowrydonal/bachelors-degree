package com.lowry.lapspace3;

import java.io.Serializable;

import org.joda.time.LocalDate;
import org.joda.time.Years;

/**
 * CLASS: LAPTOP OWNER USED IN ADMIN ACTIVITIES
 */

@SuppressWarnings("serial")

public class ClassLaptopOwner implements Serializable {

    private int id;
    private String name;
    private String email;
    private String laptop_loc;
    private String occupation;
    private String age_range;

    public ClassLaptopOwner(int laptopOwnerId, String name, String email , String laptop_loc, String occupation, String age_range) {

        id = laptopOwnerId;
        this.name = name;
        this.email = email;
        this.laptop_loc = laptop_loc;
        this.occupation = occupation;
        this.age_range = age_range;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLaptop_loc() {
        return laptop_loc;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getAgeRange() { return age_range; }
}
