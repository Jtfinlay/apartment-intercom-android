package ca.finlay.ApartmentIntercom;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
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

    private static final String TAG = "MainActivity";

    private String _srvAddress;
    private Button _btnConnect;
    private EditText _txtAddress;
    private View _vInit;
    private ProgressDialog _progress;


    private STATE _state = STATE.INIT;
    private enum STATE {
      INIT, CONNECT, VALIDATE_NUMBER, ADD_NUMBER, REMOVE_NUMBER;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO::JF If server known, check whether phone is connected

        // Layout items
        _btnConnect = (Button) findViewById(R.id.btnConnect);
        _txtAddress = (EditText) findViewById(R.id.txtAddress);
        _vInit = findViewById(R.id.viewInit);
        _progress = new ProgressDialog(this);

        _progress.setCanceledOnTouchOutside(false);

        performStateLogic();
    }

    private void performStateLogic()
    {
        switch (_state)
        {
            case INIT:
                _progress.cancel();
                _vInit.setVisibility(View.VISIBLE);
                _btnConnect.setOnClickListener(new ConnectButtonListener());
                break;

            case CONNECT:
                _progress.setTitle("Connecting...");
                _progress.show();
                Manager.GetHello(MainActivity.this, _srvAddress);
                break;

            case VALIDATE_NUMBER:
                _progress.setTitle("Validating...");
                TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                Manager.CheckNumber(MainActivity.this, _srvAddress, tMgr.getLine1Number());
                break;

            case ADD_NUMBER:
            case REMOVE_NUMBER:
                break;
        }
    }

    @Override
    public void onRequestComplete(String result) {
        try {

            JSONObject obj = new JSONObject(result);
            switch (_state) {

                case CONNECT:
                    boolean setup = obj.getBoolean("setup");

                    if (setup) {
                        _state = STATE.VALIDATE_NUMBER;
                    } else {
                        _state = STATE.INIT;
                        Toast.makeText(this, "No admin set. Please access web portal.", Toast.LENGTH_SHORT).show();
                    }
                    performStateLogic();
                    break;

                case VALIDATE_NUMBER:
                    boolean exists = obj.getBoolean("exists");

                    if (exists) {
                        Toast.makeText(this, "Already in system.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "JSON Exception. Inform developer", Toast.LENGTH_SHORT).show();

            _state = STATE.INIT;
            performStateLogic();
        }
    }

    @Override
    public void onRequestError(Exception e) {
        Toast.makeText(this, "Request Exception. Inform developer", Toast.LENGTH_SHORT).show();
        e.printStackTrace();

        _state = STATE.INIT;
        performStateLogic();
    }

    private class ConnectButtonListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
//            _srvAddress = _txtAddress.getText().toString();
            _srvAddress = "http://mind-craft.cloudapp.net";
            _state = STATE.CONNECT;
            performStateLogic();
        }
    }
}
