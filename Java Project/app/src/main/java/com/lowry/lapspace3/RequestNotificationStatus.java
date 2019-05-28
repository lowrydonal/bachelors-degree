package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * REQUEST: NOTIFICATION DETAILS
 */

public class RequestNotificationStatus extends StringRequest {
    private static final String NOTIFICATION_STATUS_REQUEST = "https://lapspacefyp.000webhostapp.com/notificationstatus.php";
    private Map<String, String> params;

    public RequestNotificationStatus(int lap_id, Response.Listener<String> listener) {
        super(Method.POST, NOTIFICATION_STATUS_REQUEST, listener, null);
        params = new HashMap<>();
        params.put("lap_id", String.valueOf(lap_id));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
