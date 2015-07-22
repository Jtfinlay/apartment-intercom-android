package ca.finlay.ApartmentIntercom.Server;

import android.os.AsyncTask;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by James on 2015-07-19.
 */
public class PostTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "PostTask";
    private static final String USER_AGENT = "Mozilla/5.0";

    private RequestListener _parent;
    private Exception exception;

    public PostTask(RequestListener parent) { _parent = parent; }

    @Override
    protected String doInBackground(String... params) {
        try
        {
            String url = params[0];
            String data = params[1];

            StringBuffer response = new StringBuffer();
            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setReadTimeout(15000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("User-Agent", USER_AGENT);

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            os.close();

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

    protected void onPostExecute(String result) {
        if (exception != null)
        {
            _parent.onRequestError(exception);
        } else
        {
            _parent.onRequestComplete(result);
        }
    }
}
