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
import com.tcs.innovations.mlcp.utilities.TagVehiclesToSlotAsync;

/**
 * Created by abhi on 2/22/2016.
 */
public class TagVehiclesToSlotActivity extends AppCompatActivity {
    private String vehicleNumber,levelId, slotName,levelName;
    private TextView textView,textView2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_vehicle);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            levelId=bundle.getString("LevelId");
            levelName=bundle.getString("LevelName");
            vehicleNumber = bundle.getString("VehicleNumber");
        }

        textView= (TextView) findViewById(R.id.heading);
        textView2= (TextView) findViewById(R.id.textView2);
        textView.setText("Level : "+levelName );
        textView.setText("Vehicle No : "+vehicleNumber );
        final TextView textView1= (TextView) findViewById(R.id.messageText);
        final EditText editText = (EditText) findViewById(R.id.vehicleNumber);
        Button done = (Button) findViewById(R.id.okButton);
        Button backButton = (Button) findViewById(R.id.backButton);
        final View view1=findViewById(R.id.view1);
        final View view2=findViewById(R.id.view2);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slot = editText.getText().toString().trim();
                if (slot.length() > 0) {
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    textView.setText("Slot : " + slot);
                    textView1.setText("Verifying slot details, please wait...");

                    TagVehiclesToSlotAsync tagVehiclesToSlotAsync = new TagVehiclesToSlotAsync(TagVehiclesToSlotActivity.this, new TagVehiclesToSlotAsync.OnService(){
                        @Override
                        public void onService(String string) {
                            Log.d("Reallocate response " , string);
                            if (string!=null){
                                Log.d("Reallocate response " , string);

                                if (string.contains("Vehicle not found")){
                                    view1.setVisibility(View.VISIBLE);
                                    view2.setVisibility(View.GONE);
                                    Toast.makeText(TagVehiclesToSlotActivity.this, "Vehicle not found,hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                }

                                else if(string.contains("Vehicle exists but slot is not booked for this vehicle")){
                                    view1.setVisibility(View.VISIBLE);
                                    view2.setVisibility(View.GONE);
                                    Toast.makeText(TagVehiclesToSlotActivity.this, "Vehicle is not booked, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                }
                                else if (string.contains("Successful")){
                                    finish();
                                    Toast.makeText(TagVehiclesToSlotActivity.this, "Slot reallocated successfully.", Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    });
                    tagVehiclesToSlotAsync.execute(slot,vehicleNumber);
                }

                else{
                    editText.setError("Please enter slot ");
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
