package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lowry on 08/12/2017.
 */

public class RequestLoginCo extends StringRequest {
    private static final String LOGIN_REQUEST_CO_URL = "https://lapspacefyp.000webhostapp.com/loginco.php";
    private Map<String, String> params;

    public RequestLoginCo(String email, String password, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_CO_URL, listener, null);
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
