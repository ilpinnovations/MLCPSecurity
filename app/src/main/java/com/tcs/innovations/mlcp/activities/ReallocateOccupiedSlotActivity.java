package com.tcs.innovations.mlcp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.utilities.ReallocateOccupiedSlotAsync;

/**
 * Created by abhi on 2/22/2016.
 */
public class ReallocateOccupiedSlotActivity extends AppCompatActivity {
    private String slotId, employeeNumber,slotName;
    private TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_vehicle);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            slotName=bundle.getString("SlotName");
            slotId = bundle.getString("SlotId");
        }

        textView= (TextView) findViewById(R.id.heading);
        textView.setText("Slot : "+ slotName);
        final TextView textView1= (TextView) findViewById(R.id.messageText);
        final EditText editText = (EditText) findViewById(R.id.vehicleNumber);
        Button done = (Button) findViewById(R.id.okButton);
        Button backButton = (Button) findViewById(R.id.backButton);
        final View view1=findViewById(R.id.view1);
        final View view2=findViewById(R.id.view2);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vehicle = editText.getText().toString().trim();
                if (vehicle.length() > 0) {
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    textView.setText("Slot : " + slotName);
                    textView1.setText("Verifying vehicle details, please wait...");

                    ReallocateOccupiedSlotAsync reallocateOccupiedSlotAsync = new ReallocateOccupiedSlotAsync(ReallocateOccupiedSlotActivity.this, new ReallocateOccupiedSlotAsync.OnService() {
                        @Override
                        public void onService(String string) {
                            Log.d("Reallocate response " , string);
                            if (string!=null){
                                Log.d("Reallocate response " , string);

                                if (string.contains("Vehicle not found")){
                                    view1.setVisibility(View.VISIBLE);
                                    view2.setVisibility(View.GONE);
                                    Toast.makeText(ReallocateOccupiedSlotActivity.this, "Vehicle not found,hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                }

                                else if(string.contains("Vehicle exists but slot is not booked for this vehicle")){
                                    view1.setVisibility(View.VISIBLE);
                                    view2.setVisibility(View.GONE);
                                    Toast.makeText(ReallocateOccupiedSlotActivity.this, "Vehicle is not booked, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                }
                                else if (string.contains("Successful")){
                                    finish();
                                    Toast.makeText(ReallocateOccupiedSlotActivity.this, "Slot reallocated successfully.", Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    });
                    reallocateOccupiedSlotAsync.execute(slotId,vehicle);
                }

                else{
                    editText.setError("Please enter vehicle number");
                }
            }


        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
