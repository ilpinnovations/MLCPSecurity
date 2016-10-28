package com.tcs.innovations.mlcp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.beans.DefaulterVehiclesBean;
import com.tcs.innovations.mlcp.beans.LiveDataBean;
import com.tcs.innovations.mlcp.utilities.DividerItemDecorator;
import com.tcs.innovations.mlcp.utilities.HttpManager;
import com.tcs.innovations.mlcp.utilities.UrlBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abhi on 2/19/2016.
 */
public class DefaulterVehicleListFragment extends Fragment {
    private ArrayList<DefaulterVehiclesBean> data = new ArrayList<>();
    RecyclerView recyclerView;

    private View view2, view3;
    private TextView messageText, errorText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
        messageText = (TextView) view.findViewById(R.id.messageText);
        errorText = (TextView) view.findViewById(R.id.errorText);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    private void getData() {
        String url = UrlBean.getUrl() + "time_excedded_asc.php";
        HttpManager httpManager = new HttpManager(getActivity(), new HttpManager.ServiceResponse() {
            @Override
            public void onServiceResponse(String serviceResponse) {
                if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                    try {
                        data = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(serviceResponse);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            DefaulterVehiclesBean defaulterVehiclesBean = new DefaulterVehiclesBean();
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            JSONObject data1 = jsonObject.getJSONObject("data1");
                            defaulterVehiclesBean.setFloorName(data1.getString("floorname"));

                            if(data1.getString("slotname").equals("null")){
                                defaulterVehiclesBean.setSlotName("Not Available");
                            }
                            else {
                                defaulterVehiclesBean.setSlotName(data1.getString("slotname"));
                            }
                            defaulterVehiclesBean.setVehicleNumber(data1.getString("vehiclenumber"));


                            JSONObject data2 = jsonObject.getJSONObject("data2");
                            String time = "";
                            String days = data2.getString("days");
                            String hours = data2.getString("hours");
                            String minutes = data2.getString("minutes");
                            time = days + " days, " + hours + " hours & " + minutes + " minutes.";
                            defaulterVehiclesBean.setParkingTime(time);

                            data.add(defaulterVehiclesBean);
                        }

                        if (data.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            view2.setVisibility(View.GONE);
                            view3.setVisibility(View.GONE);

                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.addItemDecoration(new DividerItemDecorator(getActivity(), LinearLayoutManager.VERTICAL));
                            recyclerView.setAdapter(new CustomAdapter(data));
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            view2.setVisibility(View.GONE);
                            view3.setVisibility(View.VISIBLE);
                            errorText.setText("No defaulter vehicles...");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("My Tag", "No defaulter vehicles");
                        recyclerView.setVisibility(View.GONE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.VISIBLE);
                        errorText.setText("No defaulter vehicles...");
                    }
                } else {
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
        messageText.setText("Refreshing defaulter vehicles list...");
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<DefaulterVehiclesBean> data;

        public CustomAdapter(ArrayList<DefaulterVehiclesBean> data) {
            this.data = data;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.defaulter_vehicles_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.level.setText(data.get(i).getFloorName());
            viewHolder.slotName.setText(data.get(i).getSlotName());
            viewHolder.parkingTime.setText(data.get(i).getParkingTime());
            viewHolder.vehicleNumber.setText(data.get(i).getVehicleNumber());

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView level;
            public TextView slotName;
            public TextView parkingTime;
            public TextView vehicleNumber;


            public ViewHolder(View itemView) {
                super(itemView);
                level = (TextView) itemView.findViewById(R.id.level);
                slotName = (TextView) itemView.findViewById(R.id.slot);
                parkingTime = (TextView) itemView.findViewById(R.id.time);
                vehicleNumber = (TextView) itemView.findViewById(R.id.vehicle);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
