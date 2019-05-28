package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * REQUEST: REQUESTING ALL LAPTOP OWNERS ASSOCIATED WITH THE POSTED COMPANY ID
 */

public class RequestActLapList extends StringRequest {
    private static final String ACT_LAP_LIST_REQUEST = "https://lapspacefyp.000webhostapp.com/laplistactcase.php";
    private Map<String, String> params;

    public RequestActLapList(int company_id, Response.Listener<String> listener) {
        super(Method.POST, ACT_LAP_LIST_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("company_id", String.valueOf(company_id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
