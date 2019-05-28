package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/*
  * REQUEST: LAPTOP OWNER REGISTER REQUEST EXTENDS STRING REQUEST TO ADD POST PARAMS
  *  USE POST FOR TAILORED REQUESTS AND GET FOR REGULAR STRING REQUESTS???
  */


public class RequestRegisterLap extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://lapspacefyp.000webhostapp.com/register.php";

    private Map<String, String> params;

    public RequestRegisterLap(String name, String email, String password, String occupation, String age_range,
                              String image_string, String laptop_loc, String address_line1, String address_line2, String county,
                              Response.Listener<String> listener) {

        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("occupation", occupation);
        params.put("age_range", age_range);
        params.put("image_string", image_string);
        params.put("laptop_loc", laptop_loc);
        params.put("address_line1", address_line1);
        params.put("address_line2", address_line2);
        params.put("county", county);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
