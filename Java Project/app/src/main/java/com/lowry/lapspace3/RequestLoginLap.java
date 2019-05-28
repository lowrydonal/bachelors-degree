package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * REQUEST: LOGIN
 */

public class RequestLoginLap extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "https://lapspacefyp.000webhostapp.com/login.php";
    private Map<String, String> params;

    public RequestLoginLap(String email, String password, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
