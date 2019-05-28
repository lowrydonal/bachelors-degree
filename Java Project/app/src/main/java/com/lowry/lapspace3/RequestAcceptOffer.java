package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

// REQUEST: ACCEPT OFFER

public class RequestAcceptOffer extends StringRequest{
    private static final String ACCEPT_OFFER_REQUEST = "https://lapspacefyp.000webhostapp.com/acceptoffer.php";
    private Map<String, String> params;

    public RequestAcceptOffer(int company_id, int lap_id, Response.Listener<String> listener) {
        super(Method.POST, ACCEPT_OFFER_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("company_id", String.valueOf(company_id));
        params.put("lap_id", String.valueOf(lap_id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
