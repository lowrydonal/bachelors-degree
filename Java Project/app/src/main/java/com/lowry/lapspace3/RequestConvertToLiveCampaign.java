package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

// REQUEST: CONVERT TO LIVE

public class RequestConvertToLiveCampaign extends StringRequest{
    private static final String CONVERT_TO_LIVE_REQUEST = "https://lapspacefyp.000webhostapp.com/converttolive.php";
    private Map<String, String> params;

    public RequestConvertToLiveCampaign(int id, Response.Listener<String> listener) {
        super(Method.POST, CONVERT_TO_LIVE_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("id", String.valueOf(id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
