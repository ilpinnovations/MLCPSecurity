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
import com.tcs.innovations.mlcp.utilities.ReallocateFreeslotAsync;

/**
 * Created by abhi on 2/22/2016.
 */

public class AllocateFreeslotActivity extends AppCompatActivity {
    private String slotId, slotName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_vehicle1);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            slotId = bundle.getString("SlotId");
            slotName = bundle.getString("SlotName");
        }

        TextView textView = (TextView) findViewById(R.id.heading);
        textView.setText("Slot : " + slotName);
        final View view1=findViewById(R.id.view1);
        final View view2=findViewById(R.id.view2);
        final TextView textView1= (TextView) findViewById(R.id.messageText);

        final EditText editText = (EditText) findViewById(R.id.vehicleNumber);
        Button okButton = (Button) findViewById(R.id.okButton);
        Button backButton = (Button) findViewById(R.id.backButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SlotId",slotId);
                String vehicleNumber = editText.getText().toString().trim();
                if (vehicleNumber.length() > 0) {
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    textView1.setText("Verifying vehicle details, please wait...");
                    ReallocateFreeslotAsync reallocateFreeslotAsync = new ReallocateFreeslotAsync(AllocateFreeslotActivity.this, new ReallocateFreeslotAsync.OnService() {

                        @Override
                        public void onService(String string) {
                            if (string!=null){
                                Log.d("Rellaocate response",string);
                                if (string.contains("Vehicle not found")){
                                    view1.setVisibility(View.VISIBLE);
                                    view2.setVisibility(View.GONE);
                                    Toast.makeText(AllocateFreeslotActivity.this, "Vehicle not found, operation failed", Toast.LENGTH_LONG).show();
                                }
                                else if(string.contains("Successful")){
                                    Toast.makeText(AllocateFreeslotActivity.this, "Slot reallocated successfully.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else{
                                    view1.setVisibility(View.VISIBLE);
                                    view2.setVisibility(View.GONE);
                                    Toast.makeText(AllocateFreeslotActivity.this, "Vehicle not booked, operation failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });

                    reallocateFreeslotAsync.execute(slotId,vehicleNumber);
                }
                else{
                    editText.setError("Please enter a valid vehicle number!");
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
