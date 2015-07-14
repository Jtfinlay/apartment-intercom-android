package ca.finlay.ApartmentIntercom;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.commons.validator.routines.UrlValidator;


public class MainActivity extends ActionBarActivity {

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

                UrlValidator urlValid = new UrlValidator();
                if (!urlValid.isValid(_srvAddress))
                {
                    // TODO::JF Make user aware the URL is invalid.
                    return;
                }

                // TODO::JF Try connecting to server
                // TODO::JF Check whether number is in system
                // TODO::JF If number is not in system, ask user if they want to add it.

            }
        });
    }
}
