package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

// REQUEST: EDIT PROFILE

public class RequestEditDetailsCo extends StringRequest{
    private static final String EDIT_DETAILS_REQUEST_CO_URL = "https://lapspacefyp.000webhostapp.com/editdetailsco.php";

    private Map<String, String> params;

    public RequestEditDetailsCo(int id, String name, String email, String image_string, String demographic_desc,
                                 Response.Listener<String> listener) {

        super(Method.POST, EDIT_DETAILS_REQUEST_CO_URL, listener, null);
        params = new HashMap<>();
        params.put("id", String.valueOf(id));
        params.put("name", name);
        params.put("email", email);
        params.put("image_string", image_string);
        params.put("demographic_desc", demographic_desc);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}