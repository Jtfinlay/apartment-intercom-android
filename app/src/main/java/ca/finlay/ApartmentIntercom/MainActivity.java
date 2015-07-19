package ca.finlay.ApartmentIntercom;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ca.finlay.ApartmentIntercom.Server.Manager;
import ca.finlay.ApartmentIntercom.Server.RequestListener;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements RequestListener {

    private String _srvAddress;
    private Button _btnConnect;
    private EditText _txtAddress;
    private TextView _txtMessage, _txtInfo;
    private View _vAddress, _vInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO::JF If server known, check whether phone is connected

        // Layout items
        _btnConnect = (Button) findViewById(R.id.btnAddress);
        _txtAddress = (EditText) findViewById(R.id.txtAddress);
        _txtMessage = (TextView) findViewById(R.id.txtMessage);
        _txtInfo = (TextView) findViewById(R.id.txtInfo);
        _vAddress = (View) findViewById(R.id.viewAddress);
        _vInfo = (View) findViewById(R.id.viewInfo);

        _btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                _srvAddress = _txtAddress.getText().toString();
                _srvAddress = "http://mind-craft.cloudapp.net";
                _vAddress.setVisibility(View.GONE);
                _vInfo.setVisibility(View.VISIBLE);

                _txtInfo.setText("Connecting...");
                Manager.GetHello(MainActivity.this, _srvAddress);

                // TODO::JF Check whether number is in system
                // TODO::JF If number is not in system, ask user if they want to add it.

            }
        });
    }

    private void onHelloComplete(boolean hasSetup)
    {
        if (!hasSetup) {
            _vInfo.setVisibility(View.GONE);
            _vAddress.setVisibility(View.VISIBLE);
            _txtMessage.setText("No admin set. Please access web portal.");
            return;
        }
        _txtInfo.setText("Admin set up...");

    }

    @Override
    public void onRequestComplete(String ID, String result) throws JSONException {
        if (ID.equals(Manager.HELLO)) {

             JSONObject obj = new JSONObject(result);
             onHelloComplete(obj.getBoolean("setup"));

             TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
             Manager.CheckNumber(MainActivity.this, _srvAddress, tMgr.getLine1Number());

        } else if (ID.equals(Manager.CHECKNUMBER)) {

            JSONObject obj = new JSONObject(result);
            boolean exists = obj.getBoolean("exists");

            if (exists) {
                Toast.makeText(this, "Already in system!", Toast.LENGTH_SHORT).show();
                return;
            }

            _txtInfo.setText(result);
        }
    }

    @Override
    public void onRequestError(Exception e) {
        Toast.makeText(this, "Error sending request", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }
}
