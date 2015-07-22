package ca.finlay.ApartmentIntercom.Server;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by James on 2015-07-15.
 */
public class Manager {

    private static final String TAG = "ServerManager";

    public static final String HELLO = "0";
    public static final String CHECKNUMBER = "1";

    public static void GetHello(RequestListener caller, String baseURL) {
        new GetTask(caller).execute(Manager.HELLO, baseURL + "/mobile/hello");
    }

    public static void CheckNumber(RequestListener caller, String baseURL, String phoneNumber) throws JSONException {
        new PostTask(caller).execute(Manager.CHECKNUMBER,
                baseURL + "/mobile/checknumber",
                "number="+phoneNumber);
    }
}
