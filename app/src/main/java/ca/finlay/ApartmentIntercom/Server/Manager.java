package ca.finlay.ApartmentIntercom.Server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by James on 2015-07-15.
 */
public class Manager {

    private static final String TAG = "ServerManager";

    public static final String HELLO = "0";

    public static void GetHello(RequestListener caller, String baseURL) {
        new GetTask(caller).execute(Manager.HELLO, baseURL + "/mobile/hello");
    }
}
