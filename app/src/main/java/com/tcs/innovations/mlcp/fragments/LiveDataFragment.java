package com.tcs.innovations.mlcp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.beans.LiveDataBean;
import com.tcs.innovations.mlcp.utilities.DividerItemDecorator;
import com.tcs.innovations.mlcp.utilities.HttpManager;
import com.tcs.innovations.mlcp.utilities.HttpManager1;
import com.tcs.innovations.mlcp.utilities.UrlBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by abhi on 2/19/2016.
 */

public class LiveDataFragment extends Fragment {

    private ArrayList<LiveDataBean> data = new ArrayList<>();
    private Timer timer;
    private TimerTaskHelper timerTaskHelper;
    private RecyclerView recyclerView;

    private View view2, view3, view4;
    private TextView messageText, messageText1, errorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
        view4 = view.findViewById(R.id.view4);

        messageText = (TextView) view.findViewById(R.id.messageText);
        messageText1= (TextView) view.findViewById(R.id.messageText1);
        errorText = (TextView) view.findViewById(R.id.errorText);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecorator(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getData();
    }

    private void getData() {
        timer = new Timer();
        timerTaskHelper = new TimerTaskHelper();
        timer.schedule(timerTaskHelper, 600, 10000);


    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<LiveDataBean> data;

        public CustomAdapter(ArrayList<LiveDataBean> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.parking_info_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.level.setText(data.get(i).getFloorName());
            //viewHolder.slotName.setText(data.get(i).getSlotName());
            viewHolder.vehicleNumber.setText(data.get(i).getVehicleNumber());

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView level;
            //public TextView slotName;
            public TextView vehicleNumber;


            public ViewHolder(View itemView) {
                super(itemView);
                level = (TextView) itemView.findViewById(R.id.level);
              //  slotName = (TextView) itemView.findViewById(R.id.slot);
                vehicleNumber = (TextView) itemView.findViewById(R.id.vehicle);
            }

        }
    }

    private class TimerTaskHelper extends TimerTask {

        @Override
        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    String url = UrlBean.getUrl() + "recent_allocated_cars.php";

                    @Override
                    public void run() {
                        HttpManager httpManager = new HttpManager(getActivity(), new HttpManager.ServiceResponse() {
                            @Override
                            public void onServiceResponse(String serviceResponse) {
                                if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                                    data = new ArrayList<LiveDataBean>();
                                    try {
                                        JSONArray jsonArray = new JSONArray(serviceResponse);

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            LiveDataBean liveDataBean = new LiveDataBean();
                                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                            JSONObject data1 = jsonObject.getJSONObject("data1");

                                            liveDataBean.setFloorName(data1.getString("floorname"));
                                            //liveDataBean.setSlotName(data1.getString("slotname"));
                                            liveDataBean.setVehicleNumber(data1.getString("vehiclenumber"));
                                            data.add(liveDataBean);

                                        }
                                        if (data.size() > 0) {
                                            view4.setVisibility(View.GONE);
                                            view3.setVisibility(View.GONE);

                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            recyclerView.setAdapter(new CustomAdapter(data));
                                        } else {
                                            recyclerView.setVisibility(View.GONE);
                                            view4.setVisibility(View.GONE);
                                            view3.setVisibility(View.VISIBLE);
                                            errorText.setText("No recent allocations...");
                                        }


                                    } catch (JSONException e) {
                                        recyclerView.setVisibility(View.GONE);
                                        view4.setVisibility(View.GONE);
                                        view3.setVisibility(View.VISIBLE);
                                        errorText.setText("No recent allocations...");
                                    }
                                } else {
                                    if (getActivity() != null) {
                                        //Toast.makeText(getActivity(), "No internet connection, please try again later.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });
                        recyclerView.setVisibility(View.VISIBLE);
                        httpManager.execute(url);
                        view4.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.GONE);
                        messageText1.setText("Refreshing live data...");
                    }
                });
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("My Tag", "Inside onDetach Live Data");
        timer.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
