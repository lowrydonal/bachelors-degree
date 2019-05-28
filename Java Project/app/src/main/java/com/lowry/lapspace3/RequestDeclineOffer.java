package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

// REQUEST: DECLINE OFFER

public class RequestDeclineOffer extends StringRequest{
    private static final String DECLINE_OFFER_REQUEST = "https://lapspacefyp.000webhostapp.com/declineoffer.php";
    private Map<String, String> params;

    public RequestDeclineOffer(int lap_id, Response.Listener<String> listener) {
        super(Method.POST, DECLINE_OFFER_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("lap_id", String.valueOf(lap_id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
