package com.tcs.innovations.mlcp.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.activities.LevelDetailsActivity;
import com.tcs.innovations.mlcp.beans.OccupiedSlotsBean;
import com.tcs.innovations.mlcp.utilities.DividerItemDecorator;
import com.tcs.innovations.mlcp.utilities.HttpManager;
import com.tcs.innovations.mlcp.utilities.TagVehiclesToSlotAsync;
import com.tcs.innovations.mlcp.utilities.UrlBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abhi on 2/20/2016.
 */

public class VehiclesUntaggedFragment extends Fragment {

    MaterialSearchView searchView2;
    String vehicleNumber;
    EditText editText;
    TextView textView;
    TextView textView_level;
    String slot;
    ArrayList<OccupiedSlotsBean> filteredList2;
    Button done, backButton;
    private ArrayList<OccupiedSlotsBean> data = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressDialog mDialog;
    private View view2, view3, viewupdate;
    private TextView messageText, errorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView2 = (MaterialSearchView) getActivity().findViewById(R.id.search_view2);
        done = (Button) view.findViewById(R.id.okButton);
        backButton = (Button) view.findViewById(R.id.backButton);
        editText = (EditText) view.findViewById(R.id.slotName_EditText);
        //  editText.setText();

        textView = (TextView) view.findViewById(R.id.heading);
        textView_level = (TextView) view.findViewById(R.id.level_tag);
        //textView2= (TextView) findViewById(R.id.textView2);

        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
        viewupdate = view.findViewById(R.id.viewupdate);
        messageText = (TextView) view.findViewById(R.id.messageText);
        errorText = (TextView) view.findViewById(R.id.errorText);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecorator(getActivity(), LinearLayoutManager.VERTICAL));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewupdate.setVisibility(View.GONE);
                onResume();
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slot = editText.getText().toString().trim();
                slot = LevelDetailsActivity.slotNameTwoLetter + slot;
                if (slot.length() > 0) {
                    // view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    //   textView.setText("Slot : " + slot);
                    //   textView1.setText("Verifying slot details, please wait...");
                    mDialog = new ProgressDialog(getActivity());
                    mDialog.setMessage("Loading...");
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    TagVehiclesToSlotAsync tagVehiclesToSlotAsync = new TagVehiclesToSlotAsync(getActivity(), new TagVehiclesToSlotAsync.OnService() {
                        @Override
                        public void onService(String string) {

                            mDialog.dismiss();
                            Log.d("Reallocate response ", string);
                            if (string != null) {
                                Log.d("Reallocate response ", string);

                                if (string.contains("Slot not found")) {
                                    // view1.setVisibility(View.VISIBLE);
                                    //view2.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Slot not found, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                } else if (string.contains("Slot is already occupied")) {
                                    //view1.setVisibility(View.VISIBLE);
                                    //view2.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Slot is already occupied, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                } else if (string.contains("Slot is already reserved")) {
                                    //view1.setVisibility(View.VISIBLE);
                                    //view2.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Slot is already reserved, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                } else if (string.contains("Successful")) {
                                    viewupdate.setVisibility(View.GONE);
                                    onResume();
                                    new SendPushNotification().execute(vehicleNumber);
                                    Toast.makeText(getActivity(), "Slot reallocated successfully.", Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    });
                    tagVehiclesToSlotAsync.execute(slot, vehicleNumber);
                } else {
                    editText.setError("Please enter slot ");
                }
            }


        });


        searchView2.setCursorDrawable(R.drawable.custom_cursor);
        searchView2.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filteredList2 = new ArrayList<>();
                if (newText.length() == 0) {
                    filteredList2.addAll(data);
                } else {
                    final String filterPattern = newText.toLowerCase().trim();

                    for (final OccupiedSlotsBean user : data) {
                        if (user.getVehicleNumber().toLowerCase().contains(filterPattern)) {
                            filteredList2.add(user);
                        }
                    }
                }
                recyclerView.setAdapter(new CustomAdapter(filteredList2));
                //  recyclerAdapter = new RecyclerAdapter(MainActivity.this, filteredList);
                //recyclerView.setAdapter(recyclerAdapter);

                return false;
            }
        });

        searchView2.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });

    }

    private void getData() {
        String url = UrlBean.getUrl() + "slot_details_for_floor.php?floor=" + LevelDetailsActivity.getLevelId();
        Log.d("Url", url);
        HttpManager httpManager = new HttpManager(getActivity(), new HttpManager.ServiceResponse() {
            @Override
            public void onServiceResponse(String serviceResponse) {
                if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                    Log.d("Occupied slots", serviceResponse);
                    data = new ArrayList<>();
                    try {

                        JSONArray jsonArray = new JSONArray(serviceResponse);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            OccupiedSlotsBean occupiedSlotsBean = new OccupiedSlotsBean();
                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data1");
                            if (jsonObject2.getString("slotname").equals("null")) {
                                occupiedSlotsBean.setSlotName("NA");
                                occupiedSlotsBean.setVehicleNumber(jsonObject2.getString("vehiclenumber"));
                                occupiedSlotsBean.setSlotId(jsonObject2.getString("booking_slot"));

                                data.add(occupiedSlotsBean);
                            }

                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        recyclerView.setAdapter(new CustomAdapter(data));

                    } catch (JSONException e) {
                        recyclerView.setVisibility(View.GONE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.VISIBLE);
                        errorText.setText("No Untagged vehicles...");
                    }
                } else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                    errorText.setText("No internet connection...");
                }
            }
        });

        httpManager.execute(url);
        view2.setVisibility(View.VISIBLE);
        view3.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        messageText.setText("Loading untagged vehicles...");
    }

    @Override
    public void onResume() {
        super.onResume();
        searchView2.setVisibility(View.VISIBLE);
        getData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchView2.isSearchOpen()) {
            searchView2.closeSearch();
        } else {
            super.onPause();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search1);
        searchView2.setMenuItem(item);
        // searchView.setMenuItem(item);
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<OccupiedSlotsBean> data;

        public CustomAdapter(ArrayList<OccupiedSlotsBean> data) {
            this.data = data;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.untagged_vehicles_slot_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {

            //viewHolder.slot.setText(data.get(i).getSlotName());
            viewHolder.vehicleNumber.setText(data.get(i).getVehicleNumber());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchView2.isSearchOpen()) {
                        searchView2.closeSearch();
                    }
                    vehicleNumber = data.get(i).getVehicleNumber();
                    searchView2.setVisibility(View.INVISIBLE);
                    viewupdate.setVisibility(View.VISIBLE);
                    textView.setText("Vehicle Number : " + vehicleNumber);
                    textView_level.setText("Level : " + LevelDetailsActivity.slotNameTwoLetter);
                     /*   Intent intent = new Intent(getActivity(), TagVehiclesToSlotActivity.class);

                    intent.putExtra("VehicleNumber", data.get(i).getVehicleNumber());
                    intent.putExtra("LevelId", LevelDetailsActivity.getLevelId());
                    intent.putExtra("LevelName", LevelDetailsActivity.getLevelName());
                    startActivity(intent);*/
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            //public TextView slot;
            public TextView vehicleNumber;


            public ViewHolder(View itemView) {
                super(itemView);
                //  slot = (TextView) itemView.findViewById(R.id.slot);
                vehicleNumber = (TextView) itemView.findViewById(R.id.vehicle);
            }

        }
    }

    class SendPushNotification extends AsyncTask<String, Void, Void> {


        AlertDialog.Builder alertDialogBuilder;
        HttpURLConnection conn;
        boolean posted = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg0) {

            String param1 = arg0[0];
            String param2 = slot + " is your Slot.";
            try {

                URL url = new URL("http://mymlcp.co.in/gcm/push_notification/gcm.php/?push=true");
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("vehicleNumber", param1);
                params.put("message", param2);

                Log.d("Hitting the server", "" + url);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    Log.d("Post data", String.valueOf(postData));
                }
                String urlParameters = postData.toString();
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                writer.write(urlParameters);
                writer.flush();

                BufferedReader reader = new BufferedReader
                        (new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.d("serverrespones", sb.toString());
                //  return sb.toString();
            } catch (Exception e) {
                // return new String(e.getMessage() + "Exception: null");
            }

            /*
            try {
                String link ="http://mymlcp.co.in/gcm/push_notification/gcm.php/?push=true"+
                                "&vehicleNumber="+arg0[0]+"&message="+message;

               URL url = new URL(link);

               conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(40000);
                conn.setConnectTimeout(45000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
               // int responseCode = conn.getResponseCode();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
*/
            return null;
        }
    }


}














/*


package com.tcs.innovations.mlcp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.activities.AllocateFreeslotActivity;
import com.tcs.innovations.mlcp.activities.LevelDetailsActivity;
import com.tcs.innovations.mlcp.activities.TagVehiclesToSlotActivity;
import com.tcs.innovations.mlcp.beans.OccupiedSlotsBean;
import com.tcs.innovations.mlcp.beans.SlotsBean;
import com.tcs.innovations.mlcp.utilities.DividerItemDecorator;
import com.tcs.innovations.mlcp.utilities.HttpManager;
import com.tcs.innovations.mlcp.utilities.TagVehiclesToSlotAsync;
import com.tcs.innovations.mlcp.utilities.UrlBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


 * Created by abhi on 2/20/2016.


public class VehiclesUntaggedFragment extends Fragment {

    private ArrayList<OccupiedSlotsBean> data = new ArrayList<>();
    private RecyclerView recyclerView;
    MaterialSearchView searchView2;
    String vehicleNumber;
    TextView textView;
    private View view2, view3,viewupdate;
    private TextView messageText, errorText;
    ArrayList<OccupiedSlotsBean> filteredList2;
    Button done,backButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView2 = (MaterialSearchView) getActivity().findViewById(R.id.search_view2);
      //  done = (Button) view.findViewById(R.id.okButton);
       // backButton = (Button) view.findViewById(R.id.backButton);

        //textView= (TextView) view.findViewById(R.id.heading);
        //textView2= (TextView) findViewById(R.id.textView2);

        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
      //  viewupdate=view.findViewById(R.id.viewupdate);
        messageText = (TextView) view.findViewById(R.id.messageText);
        errorText = (TextView) view.findViewById(R.id.errorText);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecorator(getActivity(), LinearLayoutManager.VERTICAL));
       backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewupdate.setVisibility(View.GONE);
                onResume();
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slot = editText.getText().toString().trim();
                if (slot.length() > 0) {
                   // view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                 //   textView.setText("Slot : " + slot);
                 //   textView1.setText("Verifying slot details, please wait...");

                    TagVehiclesToSlotAsync tagVehiclesToSlotAsync = new TagVehiclesToSlotAsync(getActivity(), new TagVehiclesToSlotAsync.OnService(){
                        @Override
                        public void onService(String string) {
                            Log.d("Reallocate response " , string);
                            if (string!=null){
                                Log.d("Reallocate response " , string);

                                if (string.contains("Slot not found")){
                                   // view1.setVisibility(View.VISIBLE);
                                    //view2.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Slot not found, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                }

                                else if(string.contains("Slot is already occupied")){
                                   // view1.setVisibility(View.VISIBLE);
                                   // view2.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Slot is already occupied, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                }
                                else if (string.contains("Successful")){
                                    viewupdate.setVisibility(View.GONE);
                                    onResume();
                                    Toast.makeText(getActivity(), "Slot reallocated successfully.", Toast.LENGTH_LONG).show();

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


        searchView2.setCursorDrawable(R.drawable.custom_cursor);
        searchView2.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filteredList2 = new ArrayList<>();
                if (newText.length() == 0) {
                    filteredList2.addAll(data);
                } else {
                    final String filterPattern = newText.toLowerCase().trim();

                    for (final OccupiedSlotsBean user : data) {
                        if (user.getVehicleNumber().toLowerCase().contains(filterPattern)) {
                            filteredList2.add(user);
                        }
                    }
                }
                recyclerView.setAdapter(new CustomAdapter(filteredList2));
                //  recyclerAdapter = new RecyclerAdapter(MainActivity.this, filteredList);
                //recyclerView.setAdapter(recyclerAdapter);

                return false;
            }
        });

        searchView2.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });

    }

    private void getData() {
        String url = UrlBean.getUrl() + "slot_details_for_floor.php?floor=" + LevelDetailsActivity.getLevelId();
        Log.d("Url", url);
        HttpManager httpManager = new HttpManager(getActivity(), new HttpManager.ServiceResponse() {
            @Override
            public void onServiceResponse(String serviceResponse) {
                if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                    Log.d("Occupied slots", serviceResponse);
                    data = new ArrayList<>();
                    try {

                        JSONArray jsonArray = new JSONArray(serviceResponse);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            OccupiedSlotsBean occupiedSlotsBean = new OccupiedSlotsBean();
                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data1");
                            if(jsonObject2.getString("slotname").equals("null")) {
                                occupiedSlotsBean.setSlotName("NA");
                                occupiedSlotsBean.setVehicleNumber(jsonObject2.getString("vehiclenumber"));
                                occupiedSlotsBean.setSlotId(jsonObject2.getString("booking_slot"));

                                data.add(occupiedSlotsBean);
                            }

                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        recyclerView.setAdapter(new CustomAdapter(data));

                    } catch (JSONException e) {
                        recyclerView.setVisibility(View.GONE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.VISIBLE);
                        errorText.setText("No Untagged vehicles...");
                    }
                } else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                    errorText.setText("No internet connection...");
                }
            }
        });

        httpManager.execute(url);
        view2.setVisibility(View.VISIBLE);
        view3.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        messageText.setText("Loading untagged vehicles...");
    }


    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<OccupiedSlotsBean> data;

        public CustomAdapter(ArrayList<OccupiedSlotsBean> data) {
            this.data = data;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.untagged_vehicles_slot_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {

            //viewHolder.slot.setText(data.get(i).getSlotName());
            viewHolder.vehicleNumber.setText(data.get(i).getVehicleNumber());

            viewHolder.submit_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchView2.isSearchOpen()) {
                        searchView2.closeSearch();
                    }
                    //vehicleNumber=data.get(i).getVehicleNumber();
                    //searchView2.setVisibility(View.INVISIBLE);
                    //viewupdate.setVisibility(View.VISIBLE);
                   // textView.setText("Vehicle Number : "+vehicleNumber);
                        Intent intent = new Intent(getActivity(), TagVehiclesToSlotActivity.class);

                    intent.putExtra("VehicleNumber", data.get(i).getVehicleNumber());
                    intent.putExtra("LevelId", LevelDetailsActivity.getLevelId());
                    intent.putExtra("LevelName", LevelDetailsActivity.getLevelName());
                    startActivity(intent);



                    String slot = viewHolder.getAdapterPosition(i).editText.getText().toString().trim();
                    if (slot.length() > 0) {
                        // view1.setVisibility(View.GONE);
                        view2.setVisibility(View.VISIBLE);
                        //   textView.setText("Slot : " + slot);
                        //   textView1.setText("Verifying slot details, please wait...");

                        TagVehiclesToSlotAsync tagVehiclesToSlotAsync = new TagVehiclesToSlotAsync(getActivity(), new TagVehiclesToSlotAsync.OnService(){
                            @Override
                            public void onService(String string) {
                                Log.d("Reallocate response " , string);
                                if (string!=null){
                                    Log.d("Reallocate response " , string);

                                    if (string.contains("Slot not found")){
                                        // view1.setVisibility(View.VISIBLE);
                                        //view2.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "Slot not found, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                    }

                                    else if(string.contains("Slot is already occupied")){
                                        // view1.setVisibility(View.VISIBLE);
                                        // view2.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "Slot is already occupied, hence operation cannot be performed.", Toast.LENGTH_LONG).show();
                                    }
                                    else if (string.contains("Successful")){
                                        viewupdate.setVisibility(View.GONE);
                                        onResume();
                                        Toast.makeText(getActivity(), "Slot reallocated successfully.", Toast.LENGTH_LONG).show();

                                    }
                                }
                            }
                        });
                        tagVehiclesToSlotAsync.execute(slot,data.get(i).getVehicleNumber());
                    }

                    else{
                        viewHolder.editText.setError("Please enter slot ");
                    }
                }






            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            //public TextView slot;
            public TextView vehicleNumber;
            EditText editText;

            ImageView submit_Image;
            public ViewHolder(View itemView) {
                super(itemView);
              //  slot = (TextView) itemView.findViewById(R.id.slot);
                editText = (EditText) itemView.findViewById(R.id.slotName_EditText);
                editText.setText(LevelDetailsActivity.slotNameTwoLetter);
                submit_Image =(ImageView) itemView.findViewById(R.id.submit_icon);

                vehicleNumber = (TextView) itemView.findViewById(R.id.vehicle);
            //    itemView.setOnClickListener();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
         getData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchView2.isSearchOpen()) {
            searchView2.closeSearch();
        } else {
            super.onPause();
        }
    }

public void submit(){

}
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search1);
        searchView2.setMenuItem(item);
        // searchView.setMenuItem(item);
    }

*/
/*
    private ArrayList<SlotsBean> data = new ArrayList<>();
    private RecyclerView recyclerView;

    private View view2,view3;
    private TextView messageText,errorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view2=view.findViewById(R.id.view2);
        view3=view.findViewById(R.id.view3);
        messageText= (TextView) view.findViewById(R.id.messageText);
        errorText= (TextView) view.findViewById(R.id.errorText);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecorator(getActivity(), LinearLayoutManager.VERTICAL));
    }

    private void getData() {
        Log.d("LevelId", LevelDetailsActivity.getLevelId());
        String url= UrlBean.getUrl()+"floor_free_slots.php?floorid="+LevelDetailsActivity.getLevelId();
        HttpManager httpManager=new HttpManager(getActivity(), new HttpManager.ServiceResponse() {
            @Override
            public void onServiceResponse(String serviceResponse) {
                if (HttpManager.getStatusCode()==200&&serviceResponse!=null){
                    data=new ArrayList<>();
                    try {
                        JSONObject jsonObject=new JSONObject(serviceResponse);
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){
                            SlotsBean slotsBean=new SlotsBean();
                            JSONObject jsonObject1= (JSONObject) jsonArray.get(i);
                            String slotName=jsonObject1.getString("slotname");
                            slotsBean.setSlotName(slotName);
                            slotsBean.setSlotId(jsonObject1.getString("slotid"));
                            data.add(slotsBean);
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        recyclerView.setAdapter(new CustomAdapter(data));
                    } catch (JSONException e) {
                        recyclerView.setVisibility(View.GONE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.VISIBLE);
                        errorText.setText("No free slots...");
                    }
                }
                else{
                    Toast.makeText(getActivity(), "No internet connection, please check your internet connection", Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                    errorText.setText("No internet connection...");
                }
            }
        });
        httpManager.execute(url);
        view2.setVisibility(View.VISIBLE);
        view3.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        messageText.setText("Loading free slots...");
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<SlotsBean> data;

        public CustomAdapter(ArrayList<SlotsBean> data) {
            this.data = data;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.slot_name_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            viewHolder.slot.setText("Slot : " + data.get(i).getSlotName());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Slot : " +data.get(i).getSlotName())
                            .setMessage("Do you want to allocate this slot?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(getActivity(), AllocateFreeslotActivity.class);
                                    intent.putExtra("SlotId",data.get(i).getSlotId());
                                    intent.putExtra("SlotName",data.get(i).getSlotName());
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView slot;
            public ViewHolder(View itemView) {
                super(itemView);
                slot = (TextView) itemView.findViewById(R.id.slotName);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }*/

