package com.tcs.innovations.mlcp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.activities.LevelDetailsActivity;
import com.tcs.innovations.mlcp.beans.LevelDetailsBean;
import com.tcs.innovations.mlcp.utilities.DividerItemDecorator;
import com.tcs.innovations.mlcp.utilities.HttpManager;
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
public class LevelListFragment extends Fragment {

    RecyclerView recyclerView;
    private ArrayList<LevelDetailsBean> data = new ArrayList<>();
    private ArrayList<LevelDetailsBean> datacomp = new ArrayList<>();
    private ArrayList<LevelDetailsBean> datatemp = new ArrayList<>();
    private Timer timer;
    private TimerTaskHelper timerTaskHelper;

    private View view2, view3, view4;
    private TextView messageText, messageText1, errorText;

    private boolean first = true;

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
        messageText1 = (TextView) view.findViewById(R.id.messageText1);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("My Tag", "Inside onDetach Level list.");
        timer.cancel();
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        ArrayList<LevelDetailsBean> data;

        public CustomAdapter(ArrayList<LevelDetailsBean> data) {
            this.data = data;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.level_row, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            viewHolder.level.setText(data.get(i).getLevelName());
            viewHolder.occupiedSLots.setText(data.get(i).getOccupiedSlots());
            viewHolder.freeSlots.setText(data.get(i).getFreeSlots());

            if (Integer.parseInt(data.get(i).getFreeSlots()) < 1) {
                viewHolder.markFreeButton.setVisibility(View.VISIBLE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LevelDetailsActivity.class);
                    intent.putExtra("Level", data.get(i).getLevelName());
                    intent.putExtra("LevelId", data.get(i).getLevelId());
                    intent.putExtra("oneslotname", data.get(i).getSlotName());
                    intent.putExtra("freeslots", data.get(i).getFreeSlots());

                    Log.d("LevelID", data.get(i).getLevelId());
                    getActivity().overridePendingTransition(R.anim.anim_translate_down, R.anim.anim_translate_up);
                    startActivity(intent);
                }
            });

            viewHolder.markFreeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String url = UrlBean.getUrl() + "update_isMarkedFreeSecurity.php?floorid=" + data.get(i).getLevelId();
                    final ProgressDialog progress = new ProgressDialog(getActivity());
                    progress.setMessage("Marking as free.");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Are you sure you want to mark this level as free.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int id) {
                                    Log.d("url", url);
                                    HttpManager httpManager = new HttpManager(getActivity(), new HttpManager.ServiceResponse() {
                                        @Override
                                        public void onServiceResponse(String serviceResponse) {
                                            progress.cancel();
                                            if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                                                Log.d("Service response: ", serviceResponse);
                                                if (serviceResponse.equalsIgnoreCase("success")) {
                                                    Toast.makeText(getActivity(), "Level successfully marked as free", Toast.LENGTH_LONG).show();
                                                    dialog.cancel();
                                                } else {
                                                    Toast.makeText(getActivity(), "Operation failed ! Error in connecting to the server.", Toast.LENGTH_LONG).show();
                                                    dialog.cancel();
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "Operation failed ! Error in connecting to the server.", Toast.LENGTH_LONG).show();
                                                dialog.cancel();
                                            }
                                        }


                                    });
                                    httpManager.execute(url);
                                    progress.show();

                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView level;
            public TextView occupiedSLots;
            public TextView freeSlots;
            public Button markFreeButton;

            public ViewHolder(View itemView) {
                super(itemView);
                level = (TextView) itemView.findViewById(R.id.level);
                occupiedSLots = (TextView) itemView.findViewById(R.id.occupiedSlots);
                freeSlots = (TextView) itemView.findViewById(R.id.freeSlots);
                markFreeButton = (Button) itemView.findViewById(R.id.markFreeButton);
            }

        }
    }

    private class TimerTaskHelper extends TimerTask {

        @Override
        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    String url = UrlBean.getUrl() + "getSlotDetails.php";

                    @Override
                    public void run() {
                        //Log.d("Level list timer task", "Level list timer task");
                        HttpManager httpManager = new HttpManager(getActivity(), new HttpManager.ServiceResponse() {
                            @Override
                            public void onServiceResponse(String serviceResponse) {
                                if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                                    data = new ArrayList<LevelDetailsBean>();
                                    try {
                                        JSONObject jsonObj = new JSONObject(serviceResponse);
                                        JSONArray jsonArray = jsonObj.getJSONArray("data");

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            LevelDetailsBean levelDetailsBean = new LevelDetailsBean();
                                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);


                                            levelDetailsBean.setLevelName(jsonObject.getString("floorName"));


                                            levelDetailsBean.setFreeSlots(jsonObject.getString("freeSlots"));
                                            levelDetailsBean.setSlotName(jsonObject.getString("slotName"));
                                            levelDetailsBean.setOccupiedSlots(jsonObject.getString("usedSlots"));
                                            levelDetailsBean.setLevelId(jsonObject.getString("floorId"));

                                            data.add(levelDetailsBean);

                                        }
                                        if (data.size() > 0) {
                                            recyclerView.setVisibility(View.VISIBLE);
                                            view4.setVisibility(View.GONE);
                                            view3.setVisibility(View.GONE);

                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            recyclerView.setAdapter(new CustomAdapter(data));
                                        } else {
                                            recyclerView.setVisibility(View.GONE);
                                            view4.setVisibility(View.GONE);
                                            view3.setVisibility(View.VISIBLE);
                                            errorText.setText("No level list available...");
                                        }


                                    } catch (JSONException e) {
                                        recyclerView.setVisibility(View.GONE);
                                        view4.setVisibility(View.GONE);
                                        view3.setVisibility(View.VISIBLE);
                                        errorText.setText("No level list available...");
                                    }
                                }
                                if (getActivity() != null) {
                                    //Toast.makeText(getActivity(), "No internet connection, please try again later.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        recyclerView.setVisibility(View.VISIBLE);
                        httpManager.execute(url);
                        view4.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.GONE);
                        messageText1.setText("Refreshing Level List...");
                    }
                });
            }
        }

    }


}
