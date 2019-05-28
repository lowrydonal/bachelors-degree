package com.lowry.lapspace3;

/**
 * Created by lowry on 04/04/2018.
 */

public class ClassNotificationDetails {
    private int company_id;
    private int lap_id;
    private String company_name;
    private String company_image_string;

    public ClassNotificationDetails(int company_id, int lap_id, String company_name, String company_image_string) {
        this.company_id = company_id;
        this.lap_id = lap_id;
        this.company_name = company_name;
        this.company_image_string = company_image_string;
    }

    public int getCompanyId() {
        return company_id;
    }

    public int getLapId() {
        return lap_id;
    }

    public String getCompanyImageString() {
        return company_image_string;
    }

    public String getCompanyName() {
        return company_name;
    }
}
