package com.tcs.innovations.mlcp.fragments;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.activities.LevelDetailsActivity;
import com.tcs.innovations.mlcp.activities.OccupiedSlotDetailActivity;
import com.tcs.innovations.mlcp.beans.OccupiedSlotsBean;
import com.tcs.innovations.mlcp.utilities.DividerItemDecorator;
import com.tcs.innovations.mlcp.utilities.HttpManager;
import com.tcs.innovations.mlcp.utilities.UrlBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abhi on 2/20/2016.
 */

public class VehiclesTaggedFragment extends Fragment {
    private ArrayList<OccupiedSlotsBean> data = new ArrayList<>();
    ArrayList<OccupiedSlotsBean> filteredList;

    private RecyclerView recyclerView;
    MaterialSearchView searchView;
    private View view2, view3;
    private TextView messageText, errorText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);

        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
        messageText = (TextView) view.findViewById(R.id.messageText);
        errorText = (TextView) view.findViewById(R.id.errorText);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecorator(getActivity(), LinearLayoutManager.VERTICAL));

        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filteredList = new ArrayList<>();
                if (newText.length() == 0) {
                    filteredList.addAll(data);
                } else {
                    final String filterPattern = newText.toLowerCase().trim();

                    for (final OccupiedSlotsBean user : data) {
                        if (user.getVehicleNumber().toLowerCase().contains(filterPattern)||user.getSlotName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(user);
                        }
                    }
                }
                recyclerView.setAdapter(new CustomAdapter(filteredList));
              //  recyclerAdapter = new RecyclerAdapter(MainActivity.this, filteredList);
                //recyclerView.setAdapter(recyclerAdapter);

                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
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
                int flag=1;
                if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                    Log.d("Occupied slots", serviceResponse);
                    data = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(serviceResponse);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            OccupiedSlotsBean occupiedSlotsBean = new OccupiedSlotsBean();
                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data1");
                            if(!jsonObject2.getString("slotname").equals("null")) {
                                occupiedSlotsBean.setSlotName(jsonObject2.getString("slotname"));
                                occupiedSlotsBean.setVehicleNumber(jsonObject2.getString("vehiclenumber"));
                                occupiedSlotsBean.setSlotId(jsonObject2.getString("booking_slot"));
                                flag =0;
                                data.add(occupiedSlotsBean);
                            }

                        }

                        recyclerView.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        recyclerView.setAdapter(new CustomAdapter(data));

                        if(flag==1) {
                            recyclerView.setVisibility(View.GONE);
                            view2.setVisibility(View.GONE);
                            view3.setVisibility(View.VISIBLE);
                            errorText.setText("No tagged vehicles...");
                        }

                    } catch (JSONException e) {
                        recyclerView.setVisibility(View.GONE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.VISIBLE);
                        errorText.setText("No tagged vehicles...");
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
        messageText.setText("Loading tagged vehicles...");
    }


    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<OccupiedSlotsBean> data;

        public CustomAdapter(ArrayList<OccupiedSlotsBean> data) {
            this.data = data;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.occupied_slot_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {

            viewHolder.slot.setText(data.get(i).getSlotName());
            viewHolder.vehicleNumber.setText(data.get(i).getVehicleNumber());
/*
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), OccupiedSlotDetailActivity.class);
                    intent.putExtra("SlotName", data.get(i).getSlotName());
                    intent.putExtra("VehicleNumber", data.get(i).getVehicleNumber());
                    intent.putExtra("SlotId", data.get(i).getSlotId());
                    startActivity(intent);
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView slot;
            public TextView vehicleNumber;


            public ViewHolder(View itemView) {
                super(itemView);
                slot = (TextView) itemView.findViewById(R.id.slot);
                vehicleNumber = (TextView) itemView.findViewById(R.id.vehicle);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search1);
        searchView.setMenuItem(item);
       // searchView.setMenuItem(item);
    }

    @Override
    public void onPause() {
        super.onPause();
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            } else {
                super.onPause();
            }
        }

}
