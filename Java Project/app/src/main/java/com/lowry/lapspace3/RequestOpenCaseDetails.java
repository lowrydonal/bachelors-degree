package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * REQUEST: OPEN CASE DETAILS REQUEST
  */

public class RequestOpenCaseDetails extends StringRequest {
    private static final String OPEN_CASE_DETAILS_REQUEST = "https://lapspacefyp.000webhostapp.com/opencasedetails.php";
    private Map<String, String> params;

    public RequestOpenCaseDetails(int id, Response.Listener<String> listener) {
        super(Method.POST, OPEN_CASE_DETAILS_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("id", String.valueOf(id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
