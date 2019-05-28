package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

// REQUEST: RETRIEVE TEMPLATE IMAGE CORRESPONDING TO CURRENT USER TO COMPARE TO INPUTTED IMAGE

public class RequestRetrieveTempImg extends StringRequest {
    private static final String RETRIEVE_TEMP_IMG_REQUEST = "https://lapspacefyp.000webhostapp.com/retrievetempimg.php";
    private Map<String, String> params;

    public RequestRetrieveTempImg(int company_id, Response.Listener<String> listener) {
        super(Method.POST, RETRIEVE_TEMP_IMG_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("company_id", String.valueOf(company_id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
