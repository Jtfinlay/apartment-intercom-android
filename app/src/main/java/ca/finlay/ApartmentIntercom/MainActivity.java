package ca.finlay.ApartmentIntercom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    private String _srvAddress, _phoneNumber;
    private Button _btnConnect, _btnRemove;
    private EditText _txtAddress;
    private View _vInit, _vComplete;
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
        _btnRemove = (Button) findViewById(R.id.btnRemove);
        _txtAddress = (EditText) findViewById(R.id.txtAddress);
        _vInit = findViewById(R.id.viewInit);
        _vComplete = findViewById(R.id.viewComplete);
        _progress = new ProgressDialog(this);

        _progress.setCanceledOnTouchOutside(false);
        _btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            _srvAddress = _txtAddress.getText().toString();
                _srvAddress = "http://mind-craft.cloudapp.net";
                _state = STATE.CONNECT;
                performStateLogic();
            }
        });
        _btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _progress.setTitle("Removing...");
                _progress.show();
                Manager.RemoveNumber(MainActivity.this, _srvAddress, _phoneNumber);
            }
        });

        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        _phoneNumber = tMgr.getLine1Number();

        performStateLogic();
    }

    private void performStateLogic()
    {
        switch (_state)
        {
            case INIT:
                _progress.cancel();
                _vInit.setVisibility(View.VISIBLE);
                _vComplete.setVisibility(View.GONE);
                break;

            case CONNECT:
                _progress.setTitle("Connecting...");
                _progress.show();
                Manager.GetHello(MainActivity.this, _srvAddress);
                break;

            case VALIDATE_NUMBER:
                _progress.setTitle("Validating...");
                Manager.CheckNumber(MainActivity.this, _srvAddress, _phoneNumber);
                break;

            case ADD_NUMBER:
                _progress.cancel();
                openAddNumberDialog();
                break;

            case REMOVE_NUMBER:
                _progress.cancel();
                _vInit.setVisibility(View.GONE);
                _vComplete.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void openAddNumberDialog()
    {
        new AlertDialog.Builder(this).setTitle("Add number")
            .setMessage("Add your number to the system?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _progress.setTitle("Adding...");
                    _progress.show();
                    Manager.AddNumber(MainActivity.this, _srvAddress, _phoneNumber);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _state = STATE.INIT;
                    performStateLogic();
                }
            }).create().show();
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
                        _state = STATE.REMOVE_NUMBER;
                    } else {
                        _state = STATE.ADD_NUMBER;
                    }
                    performStateLogic();
                    break;

                case ADD_NUMBER:
                    boolean success_add = obj.getBoolean("success");

                    String msg = success_add ? "Added to the system" : "Error, please contact developer";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                    _state = success_add ? STATE.REMOVE_NUMBER : STATE.INIT;
                    performStateLogic();
                    break;

                case REMOVE_NUMBER:
                    boolean success_del = obj.getBoolean("success");
                    String messg = success_del ? "Removed from system" : "Error, please contact developer";

                    Toast.makeText(this, messg, Toast.LENGTH_SHORT).show();

                    _state = STATE.INIT;
                    performStateLogic();
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
}
