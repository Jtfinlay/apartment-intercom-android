package ca.finlay.ApartmentIntercom;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ca.finlay.ApartmentIntercom.Server.Manager;
import ca.finlay.ApartmentIntercom.Server.RequestListener;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements RequestListener {

    private String _srvAddress;
    private Button _btnConnect;
    private EditText _txtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO::JF If server known, check whether phone is connected

        // Layout items
        _btnConnect = (Button) findViewById(R.id.btnAddress);
        _txtAddress = (EditText) findViewById(R.id.txtAddress);

        _btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _srvAddress = _txtAddress.getText().toString();

//                UrlValidator urlValid = new UrlValidator();
//                if (!urlValid.isValid(_srvAddress))
//                {
//                    Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                Manager.GetHello(MainActivity.this, "http://mind-craft.cloudapp.net");


//                ServerManager.getHello("http://mind-craft.cloudapp.net");
                // TODO::JF Try connecting to server
                // TODO::JF Check whether number is in system
                // TODO::JF If number is not in system, ask user if they want to add it.

            }
        });
    }

    @Override
    public void onRequestComplete(String ID, String result) {
        if (ID.equals(Manager.HELLO)) {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestError(Exception e) {
        e.printStackTrace();
    }
}
