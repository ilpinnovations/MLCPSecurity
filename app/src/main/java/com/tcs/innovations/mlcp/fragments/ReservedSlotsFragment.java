package com.tcs.innovations.mlcp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.activities.LevelDetailsActivity;
import com.tcs.innovations.mlcp.beans.SlotsBean;
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
public class ReservedSlotsFragment extends Fragment {

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
        String url= UrlBean.getUrl()+"floor_reserved_slots.php?floorid="+LevelDetailsActivity.getLevelId();
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
                        errorText.setText("No reserved slots...");
                    }
                }
                else{
                    Toast.makeText(getActivity(),"No internet connection",Toast.LENGTH_LONG).show();
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
        messageText.setText("Loading reserved slots...");
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
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.slot.setText("Slot : " + data.get(i).getSlotName());
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
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
