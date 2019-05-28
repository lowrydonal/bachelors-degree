package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

// REQUEST: COMPANY REGISTER

public class RequestRegisterCo extends StringRequest{
    private static final String REGISTER_REQUEST_CO_URL = "https://lapspacefyp.000webhostapp.com/registerco.php";

    private Map<String, String> params;

    public RequestRegisterCo(String name, String email, String password, String image_string, String demographic_desc, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_CO_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("image_string", image_string);
        params.put("demographic_desc", demographic_desc);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
