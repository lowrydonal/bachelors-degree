package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * REQUEST: NOTIFICATION DETAILS
 */

public class RequestNotificationDetails extends StringRequest {
    private static final String NOTIFICATION_DETAILS_REQUEST = "https://lapspacefyp.000webhostapp.com/notificationdetails.php";
    private Map<String, String> params;

    public RequestNotificationDetails(int request_id, Response.Listener<String> listener) {
        super(Method.POST, NOTIFICATION_DETAILS_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("request_id", String.valueOf(request_id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
