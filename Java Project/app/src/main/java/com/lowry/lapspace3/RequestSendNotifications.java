package com.lowry.lapspace3;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

// REQUEST: SEND NOTIFICATIONS

public class RequestSendNotifications extends StringRequest{
    private static final String SEND_NOTIFICATIONS_URL = "https://lapspacefyp.000webhostapp.com/sendnotifications.php";

    private Map<String, String> params;

    public RequestSendNotifications(int company_id, String laptop_owner_array, Response.Listener<String> listener) {
        super(Method.POST, SEND_NOTIFICATIONS_URL, listener, null);
        params = new HashMap<>();
        params.put("company_id", String.valueOf(company_id));
        params.put("laptop_owner_array", laptop_owner_array);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
