package com.tcs.innovations.mlcp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.utilities.HttpManager;
import com.tcs.innovations.mlcp.utilities.UrlBean;

/**
 * Created by abhi on 7/13/2016.
 */
public class AddErroneousVehicleActivity extends AppCompatActivity {

    private EditText mEditText1, mEditText2;
    private Button mButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_erroneous_vehicle);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mEditText1 = (EditText) findViewById(R.id.input_VehicleNumber);
        mEditText2 = (EditText) findViewById(R.id.input_levelDetails);
        mButton1 = (Button) findViewById(R.id.submit);

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = new ProgressDialog(AddErroneousVehicleActivity.this);
                progress.setMessage("Connecting to the server.");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                String vehicleNumber = mEditText1.getText().toString().trim();
                String level = mEditText2.getText().toString().trim();

                if (vehicleNumber.length() == 0) {
                    mEditText1.setError("Please enter a valid vehicle number.");
                } else if (level.length() == 0) {
                    mEditText2.setError("Please enter a valid level.");
                } else {
                    String url = UrlBean.getUrl() + "add_ErroneousParking.php?vehicle_no=" + vehicleNumber.replace(" ", "%20") + "&is_parked_at=" + level.replace(" ", "%20");

                    HttpManager httpManager = new HttpManager(AddErroneousVehicleActivity.this, new HttpManager.ServiceResponse() {
                        @Override
                        public void onServiceResponse(String serviceResponse) {
                            progress.cancel();
                            if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                                if (serviceResponse.equalsIgnoreCase("success")) {
                                    Log.d("Service Response", serviceResponse);
                                    Toast.makeText(AddErroneousVehicleActivity.this, "Details successfully submitted to the admin.", Toast.LENGTH_LONG).show();
                                    progress.cancel();
                                    finish();
                                } else {
                                    Toast.makeText(AddErroneousVehicleActivity.this, "Error while connecting to the server!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(AddErroneousVehicleActivity.this, "Error while connecting to the server!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    httpManager.execute(url);
                    progress.show();
                    Log.d("Url", url);
                }

            }
        });
    }
}