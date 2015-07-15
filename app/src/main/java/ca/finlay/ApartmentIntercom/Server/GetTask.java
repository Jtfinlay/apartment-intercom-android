package ca.finlay.ApartmentIntercom.Server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by James on 2015-07-15.
 */
public class GetTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "HelloTask";
    private static final String USER_AGENT = "Mozilla/5.0";

    private RequestListener _parent;
    private Exception exception;
    private String _id;


    public GetTask(RequestListener parent)
    {
        _parent = parent;
    }

    @Override
    protected String doInBackground(String... urls)
    {
        try
        {
            _id = urls[0];
            String url = urls[1];

            StringBuffer response = new StringBuffer();
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    protected void onPostExecute(String string) {
        if (exception != null)
        {
            _parent.onRequestError(exception);
        } else
        {
            _parent.onRequestComplete(_id, string);
        }

    }


}
