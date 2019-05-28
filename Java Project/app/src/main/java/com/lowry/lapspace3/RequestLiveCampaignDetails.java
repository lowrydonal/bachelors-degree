package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * REQUEST: LIVE CAMPAIGN DETAILS REQUEST
 */

public class RequestLiveCampaignDetails extends StringRequest {
    private static final String LIVE_CAMPAIGN_DETAILS_REQUEST = "https://lapspacefyp.000webhostapp.com/livecampaigndetails.php";
    private Map<String, String> params;

    public RequestLiveCampaignDetails(int id, Response.Listener<String> listener) {
        super(Method.POST, LIVE_CAMPAIGN_DETAILS_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("id", String.valueOf(id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
