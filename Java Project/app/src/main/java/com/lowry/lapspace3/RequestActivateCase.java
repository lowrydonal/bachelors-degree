package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

// REQUEST: ACTIVATE CASE IN DB

public class RequestActivateCase extends StringRequest{
    private static final String ACTIVATE_CASE_REQUEST = "https://lapspacefyp.000webhostapp.com/activatecase.php";
    private Map<String, String> params;

    public RequestActivateCase(int id, Response.Listener<String> listener) {
        super(Method.POST, ACTIVATE_CASE_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("id", String.valueOf(id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
