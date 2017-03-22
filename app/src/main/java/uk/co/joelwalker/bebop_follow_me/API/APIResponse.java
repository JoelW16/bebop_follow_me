package uk.co.joelwalker.bebop_follow_me.API;

import org.json.JSONObject;

/**
 * Created by Joel on 22/03/2017.
 */

public interface APIResponse {
    void apiResponse(boolean connected, JSONObject res);
}
