package ca.finlay.ApartmentIntercom.Server;

/**
 * Created by James on 2015-07-15.
 */
public class Manager {

    private static final String TAG = "ServerManager";

    public static final String HELLO = "0";
    public static final String CHECKNUMBER = "1";

    public static void GetHello(RequestListener caller, String baseURL) {
        new GetTask(caller).execute(baseURL + "/mobile/hello");
    }

    public static void CheckNumber(RequestListener caller, String baseURL, String phoneNumber) {
        new PostTask(caller).execute(baseURL + "/mobile/checknumber","number="+phoneNumber);
    }
}
