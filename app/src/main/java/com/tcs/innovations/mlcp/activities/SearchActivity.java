package com.tcs.innovations.mlcp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.utilities.SearchSlotAsync;
import com.tcs.innovations.mlcp.utilities.SearchVehicleAsync;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhi on 2/21/2016.
 */
public class SearchActivity extends AppCompatActivity {
    private RadioButton radioButton1, radioButton2;
    private Button button1, button2, button3;
    private EditText editText1;
    private View view1, view2, view3;
    private TextView textView1,textView2,textView3,textView4,textView5;
    ImageView imageView1,imageView2,imageView3,imageView4;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);

        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);

        button1 = (Button) findViewById(R.id.searchButton);
        button2 = (Button) findViewById(R.id.button1);
        button3 = (Button) findViewById(R.id.button2);

        editText1 = (EditText) findViewById(R.id.searchEditText);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);

        textView1= (TextView) findViewById(R.id.headerText);
        textView2= (TextView) findViewById(R.id.result1);
        textView3= (TextView) findViewById(R.id.result2);
        textView4= (TextView) findViewById(R.id.result3);
        textView5= (TextView) findViewById(R.id.result4);

        imageView1= (ImageView) findViewById(R.id.img1);
        imageView2= (ImageView) findViewById(R.id.img2);
        imageView3= (ImageView) findViewById(R.id.img3);
        imageView4= (ImageView) findViewById(R.id.img4);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton1.isChecked()) {
                    String searchText = editText1.getText().toString().trim();
                    if (searchText.length() > 0) {
                        SearchSlotAsync searchSlotAsync=new SearchSlotAsync(SearchActivity.this, new SearchSlotAsync.OnService() {
                            @Override
                            public void onService(String string) {
                                if (string!=null){
                                    Log.d("Search result",string);

                                    try {
                                        JSONObject jsonObject=new JSONObject(string);
                                        String flag=jsonObject.getString("is_exist");
                                        Log.d("Flag",flag);

                                        if (flag.equalsIgnoreCase("true")){

                                            textView1.setText("Search results");

                                            String isBooked=jsonObject.getString("flag");
                                            view1.setVisibility(View.GONE);
                                            view2.setVisibility(View.GONE);
                                            view3.setVisibility(View.VISIBLE);

                                            if (isBooked.equalsIgnoreCase("is_booked")){
                                                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                                                imageView1.setImageResource(R.drawable.slot_image);
                                                imageView1.setVisibility(View.VISIBLE);

                                                textView2.setText("Slot name : " + jsonObject1.getString("slotname"));
                                                textView2.setVisibility(View.VISIBLE);

                                                imageView2.setImageResource(R.drawable.user_image);
                                                imageView2.setVisibility(View.VISIBLE);

                                                /*textView3.setText("Employee : " + jsonObject1.getString("name"));
                                                textView3.setVisibility(View.VISIBLE);

                                                imageView3.setImageResource(R.drawable.phone_image);
                                                imageView3.setVisibility(View.VISIBLE);

                                                textView4.setText("Contact : \n" + jsonObject1.getString("mobilenumber"));
                                                textView4.setVisibility(View.VISIBLE);*/

                                                imageView3.setImageResource(R.drawable.car_image1);
                                                imageView3.setVisibility(View.VISIBLE);

                                                textView3.setText("Vehicle number : \n" + jsonObject1.getString("vehiclenumber"));
                                                textView3.setVisibility(View.VISIBLE);

                                            }

                                            else if (isBooked.equalsIgnoreCase("is_empty")){
                                                Log.d("Is in is_empty","Is Empty");
                                                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                                                imageView1.setImageResource(R.drawable.slot_image);
                                                imageView1.setVisibility(View.VISIBLE);

                                                textView2.setText("Slot name : " + jsonObject1.getString("slotname"));
                                                textView2.setVisibility(View.VISIBLE);

                                                imageView2.setImageResource(R.drawable.car_image1);
                                                imageView2.setVisibility(View.VISIBLE);

                                                textView3.setText("Vehicle size : " + jsonObject1.getString("vehiclesize"));
                                                textView3.setVisibility(View.VISIBLE);

                                            }
                                        }

                                        else{
                                            view3.setVisibility(View.GONE);
                                            view2.setVisibility(View.GONE);
                                            view1.setVisibility(View.VISIBLE);
                                            Toast.makeText(getApplicationContext(),"Slot does not exists.",Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_LONG).show();
                                    view3.setVisibility(View.GONE);
                                    view2.setVisibility(View.GONE);
                                    view1.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        searchSlotAsync.execute(searchText);
                        view1.setVisibility(View.GONE);
                        view2.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.GONE);

                    } else {
                        editText1.setError("Please enter a valid slot!");
                    }
                }
                else if(radioButton2.isChecked()){
                    String searchText = editText1.getText().toString().trim();
                    if (searchText.length() > 0) {
                        SearchVehicleAsync searchVehicleAsync=new SearchVehicleAsync(SearchActivity.this, new SearchVehicleAsync.OnService() {
                            @Override
                            public void onService(String string) {
                                if (string!=null){
                                    Log.d("Search result",string);

                                    try {
                                        JSONObject jsonObject=new JSONObject(string);
                                        if (jsonObject.getString("is_exist").equalsIgnoreCase("true")){

                                            view1.setVisibility(View.GONE);
                                            view2.setVisibility(View.GONE);
                                            view3.setVisibility(View.VISIBLE);

                                            if (jsonObject.getString("flag").equalsIgnoreCase("is_booked")){
                                                JSONObject jsonObject1=jsonObject.getJSONObject("data");

                                                textView2.setText("Slot name: " + jsonObject1.getString("slotname"));
                                                textView2.setVisibility(View.VISIBLE);

                                                imageView1.setImageResource(R.drawable.slot_image);
                                                imageView1.setVisibility(View.VISIBLE);

                                                textView3.setText("Employee : " + jsonObject1.getString("name"));
                                                textView3.setVisibility(View.VISIBLE);

                                                imageView2.setImageResource(R.drawable.user_image);
                                                imageView2.setVisibility(View.VISIBLE);

                                                textView4.setText("Contact : " + jsonObject1.getString("mobilenumber"));
                                                textView4.setVisibility(View.VISIBLE);

                                                imageView3.setImageResource(R.drawable.phone_image);
                                                imageView3.setVisibility(View.VISIBLE);

                                            }

                                            else if (jsonObject.getString("flag").equalsIgnoreCase("is_vehicle")){
                                                JSONObject jsonObject1=jsonObject.getJSONObject("data1");
                                                JSONObject jsonObject2=jsonObject.getJSONObject("data2");

                                                textView2.setText("Vehicle number : " + jsonObject1.getString("vehiclenumber"));
                                                textView2.setVisibility(View.VISIBLE);

                                                imageView1.setImageResource(R.drawable.car_image1);
                                                imageView1.setVisibility(View.VISIBLE);

                                                textView3.setText("Employee: " + jsonObject2.getString("name"));
                                                textView3.setVisibility(View.VISIBLE);

                                                imageView2.setImageResource(R.drawable.user_image);
                                                imageView2.setVisibility(View.VISIBLE);

                                                textView4.setText("Employee Id : " + jsonObject2.getString("employeeid"));
                                                textView4.setVisibility(View.VISIBLE);

                                                imageView3.setImageResource(R.drawable.user_image);
                                                imageView3.setVisibility(View.VISIBLE);

                                                textView5.setText("Contact : " + jsonObject2.getString("mobilenumber"));
                                                textView5.setVisibility(View.VISIBLE);

                                                imageView4.setImageResource(R.drawable.phone_image);
                                                imageView4.setVisibility(View.VISIBLE);

                                            }
                                        }
                                        else{
                                            view3.setVisibility(View.GONE);
                                            view2.setVisibility(View.GONE);
                                            view1.setVisibility(View.VISIBLE);
                                            Toast.makeText(getApplicationContext(),"Vehicle not registered",Toast.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                        searchVehicleAsync.execute(searchText);
                        view1.setVisibility(View.GONE);
                        view2.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.GONE);

                    } else {
                        editText1.setError("Please enter a valid vehicle number!");
                    }
                }
            }
        });
    }

}
