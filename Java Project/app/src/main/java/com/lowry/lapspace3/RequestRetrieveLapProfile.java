package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * REQUEST: RETRIEVE LAPTOP OWNER PROFILE DETAILS
 */

public class RequestRetrieveLapProfile extends StringRequest {
    private static final String RETRIEVE_LAP_REQUEST_URL = "https://lapspacefyp.000webhostapp.com/retrievelapprofile.php";
    private Map<String, String> params;

    public RequestRetrieveLapProfile(int lap_id, Response.Listener<String> listener) {
        super(Method.POST, RETRIEVE_LAP_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("lap_id", String.valueOf(lap_id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
