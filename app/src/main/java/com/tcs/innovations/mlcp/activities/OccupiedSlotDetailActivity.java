package com.tcs.innovations.mlcp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.utilities.HttpManager;
import com.tcs.innovations.mlcp.utilities.UrlBean;

/**
 * Created by abhi on 2/22/2016.
 */

public class OccupiedSlotDetailActivity extends AppCompatActivity {
    private String slotName, vehicleNumber, levelName, slotId;
    private int numOfClicks = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.occupied_slot_details);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            slotName = bundle.getString("SlotName");
            vehicleNumber = bundle.getString("VehicleNumber");
            slotId = bundle.getString("SlotId");
        }

        levelName = LevelDetailsActivity.getLevelName();

        TextView level = (TextView) findViewById(R.id.floorName);
        level.setText(levelName);
        TextView slot = (TextView) findViewById(R.id.slotName);
        slot.setText(slotName);
        TextView vehicle = (TextView) findViewById(R.id.vehicleNumber);
        vehicle.setText(vehicleNumber);


        final TextView messageText = (TextView) findViewById(R.id.messageText);
        final View view1 = findViewById(R.id.view1);
        final View view2 = findViewById(R.id.view2);

        Button reallocate = (Button) findViewById(R.id.reallocateButton);
        Button release = (Button) findViewById(R.id.releaseButton);

        reallocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OccupiedSlotDetailActivity.this, ReallocateOccupiedSlotActivity.class);
                intent.putExtra("SlotId", slotId);
                intent.putExtra("SlotName", slotName);
                startActivity(intent);
            }
        });

        release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numOfClicks != 3) {
                    ++numOfClicks;
                    int pendingNumOfClicks = 4 - numOfClicks;

                    if (pendingNumOfClicks > 1) {
                        Toast.makeText(OccupiedSlotDetailActivity.this, "Tap release slot for " + pendingNumOfClicks + " times.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OccupiedSlotDetailActivity.this, "Tap release slot for " + pendingNumOfClicks + " more time.", Toast.LENGTH_SHORT).show();
                    }

                } else if (numOfClicks == 3) {
                    numOfClicks = 0;
                    new AlertDialog.Builder(OccupiedSlotDetailActivity.this)
                            .setTitle("Release slot")
                            .setMessage("Do you really want to release this slot?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    view1.setVisibility(View.GONE);
                                    view2.setVisibility(View.VISIBLE);
                                    messageText.setText("Releasing slot...");

                                    String url = UrlBean.getUrl() + "release_slot_when_occupied.php?slot_id=" + slotId + "&vehiclenumber=" + vehicleNumber;
                                    Log.d("URL: ", url);
                                    HttpManager httpManager = new HttpManager(OccupiedSlotDetailActivity.this, new HttpManager.ServiceResponse() {
                                        @Override
                                        public void onServiceResponse(String serviceResponse) {
                                            if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                                                //Log.d(" Release slot: status", serviceResponse);
                                                if (serviceResponse.equalsIgnoreCase("Slot Released")) {
                                                    Toast.makeText(OccupiedSlotDetailActivity.this, "Slot released...", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    view1.setVisibility(View.VISIBLE);
                                                    view2.setVisibility(View.GONE);
                                                    Toast.makeText(OccupiedSlotDetailActivity.this, "Operation failed please try again later...", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                view1.setVisibility(View.VISIBLE);
                                                view2.setVisibility(View.GONE);
                                                Toast.makeText(OccupiedSlotDetailActivity.this, "No internet connection...", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    httpManager.execute(url);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();

                }


            }
        });


    }
}
