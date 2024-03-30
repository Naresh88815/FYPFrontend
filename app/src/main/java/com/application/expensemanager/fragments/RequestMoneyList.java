//package com.application.expensemanager.fragments;
//
//import static com.application.expensemanager.utils.MyApplication.apinetwork;
//import static com.application.expensemanager.utils.MyApplication.mSp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import androidx.appcompat.widget.SearchView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.application.expensemanager.R;
//import com.application.expensemanager.activity.RequestDetailsActivity;
//import com.application.expensemanager.adapter.ReceivedRequestAdapter;
//import com.application.expensemanager.adapter.RequestMoneyAdapter;
//import com.application.expensemanager.model.RequestMoneyModel;
//import com.application.expensemanager.network.VolleyResponse;
//import com.application.expensemanager.utils.Constants;
//import com.application.expensemanager.utils.MyApplication;
//import com.application.expensemanager.utils.SPCsnstants;
//import com.application.expensemanager.utils.Utils;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//
//public class RequestMoneyList extends Fragment {
//    View view;
//    RecyclerView recyclerView;
//    List<RequestMoneyModel> requestMoneyModelList = new ArrayList<>();
//    RequestMoneyAdapter requestMoneyAdapter;
//    SearchView searchLabel;
//    List<RequestMoneyModel> filteredList = new ArrayList<>();
//
//    String paymentStatus, exp_id, amount, label, image, date;
//    TextView no_data;
//    Spinner sort_spinner, sort_by_date, sort_by_amt;
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        getExpenseView();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.request_for_money_list, container, false);
//        initView();
//
//        requestMoneyAdapter.setOnItemClickListener(new RequestMoneyAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(RequestMoneyModel model) {
//                exp_id = model.getId(); // Assuming there is a method to get id from RequestMoneyModel
//                Intent intent = new Intent(getContext(), RequestDetailsActivity.class);
//                intent.putExtra("exp_id", exp_id);
//                startActivity(intent);
//            }
//        });
//
//        searchLabel.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String value) {
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String value) {
//                performSearch(value);
//                return true;
//            }
//        });
//
////        // Set up the ArrayAdapter for the Spinner
////        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
////                getContext(),
////                R.array.sort_by_options,
////                android.R.layout.simple_spinner_item
////        );
////
////        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////
////        // Set a prompt to be displayed in the Spinner
////        sort_spinner.setPrompt(getString(R.string.sort_by_prompt));
////        sort_spinner.setAdapter(adapter);
////
////        // Handle item selection in the Spinner
////        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
////                // Handle item selection based on the selected position
//////                handleSortItemSelected(position);
////            }
////
////            @Override
////            public void onNothingSelected(AdapterView<?> parentView) {
////                // Do nothing here if needed
////            }
////        });
//
////        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
////                getContext(),
////                R.array.sort_by_date,
////                android.R.layout.simple_spinner_item
////        );
////        sort_by_date.setAdapter(adapter1);
////        sort_by_date.setPrompt(getString(R.string.sort_by_date));
////        sort_by_date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////
////            }
////
////            @Override
////            public void onNothingSelected(AdapterView<?> parent) {
////
////            }
////        });
////
////        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
////                getContext(),
////                R.array.sort_by_amt,
////                android.R.layout.simple_spinner_item
////        );
////        sort_by_amt.setAdapter(adapter2);
////        sort_by_amt.setPrompt(getString(R.string.sort_by_amt));
////        sort_by_amt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////            @Override
////            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                handleAmtSortItemSelected(position);
////            }
////
////            @Override
////            public void onNothingSelected(AdapterView<?> parent) {
////
////            }
////        });
////
//        return view;
//    }
//
//    public void initView() {
//        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//
//        requestMoneyAdapter = new RequestMoneyAdapter(requestMoneyModelList, getContext());
//        recyclerView.setAdapter(requestMoneyAdapter);
//
//        searchLabel = view.findViewById(R.id.searchLabel);
//        no_data = view.findViewById(R.id.no_data);
////        sort_spinner = view.findViewById(R.id.sort_spinner);
////        sort_by_date = view.findViewById(R.id.sort_by_date);
////        sort_by_amt = view.findViewById(R.id.sort_by_amt);
//
//    }
//
////    private void sortByOptions(int position){
////        switch (position) {
////            case 0:
////                Collections.sort(requestMoneyModelList, new Comparator<RequestMoneyModel>() {
////                    @Override
////                    public int compare(RequestMoneyModel o1, RequestMoneyModel o2) {
////                        // Sort by "Request By" in alphabetical order
////                        Collections.sort(requestMoneyModelList, new Comparator<RequestMoneyModel>() {
////                            @Override
////                            public int compare(RequestMoneyModel o1, RequestMoneyModel o2) {
////                                // Assuming getRequestByTV() is a method in RequestMoneyModel to get "Request By" value
////                                return o1.getRequestByTV().compareToIgnoreCase(o2.getRequestByTV());
////                            }
////                        });
////                    }
////                });
////                requestMoneyAdapter.notifyDataSetChanged();
////                break;
////            case 1:
////                Collections.sort(requestMoneyModelList, new Comparator<RequestMoneyModel>() {
////                    @Override
////                    public int compare(RequestMoneyModel o1, RequestMoneyModel o2) {
////                        // Convert string amounts to double for proper comparison
////                        double amount1 = Double.parseDouble(o1.getAmountTV());
////                        double amount2 = Double.parseDouble(o2.getAmountTV());
////                        // Use Double.compare for proper comparison of double values
////                        return Double.compare(amount2, amount1);
////                    }
////                });
////                requestMoneyAdapter.notifyDataSetChanged();
////                break;
////            case  3:
////
////                break;
////        }
////    }
////
////    private void handleAmtSortItemSelected(int position) {
////        // Implement logic based on the selected position
////        switch (position) {
////            case 0:
////                Collections.sort(requestMoneyModelList, new Comparator<RequestMoneyModel>() {
////                    @Override
////                    public int compare(RequestMoneyModel o1, RequestMoneyModel o2) {
////                        // Convert string amounts to double for proper comparison
////                        double amount1 = Double.parseDouble(o1.getAmountTV());
////                        double amount2 = Double.parseDouble(o2.getAmountTV());
////                        // Use Double.compare for proper comparison of double values
////                        return Double.compare(amount1, amount2);
////                    }
////                });
////                requestMoneyAdapter.notifyDataSetChanged();
////                break;
////            case 1:
////                Collections.sort(requestMoneyModelList, new Comparator<RequestMoneyModel>() {
////                    @Override
////                    public int compare(RequestMoneyModel o1, RequestMoneyModel o2) {
////                        // Convert string amounts to double for proper comparison
////                        double amount1 = Double.parseDouble(o1.getAmountTV());
////                        double amount2 = Double.parseDouble(o2.getAmountTV());
////                        // Use Double.compare for proper comparison of double values
////                        return Double.compare(amount2, amount1);
////                    }
////                });
////                requestMoneyAdapter.notifyDataSetChanged();
////                break;
////        }
////    }
////
////    private void handleSortDateItemSelected(int position) {
////        // Implement logic based on the selected position
////        switch (position) {
////            case 0:
////
////                requestMoneyAdapter.notifyDataSetChanged();
////                break;
////            case 1:
////                // Handle Option 2
////                break;
////            case 2:
////                // Handle Option 3
////                break;
////            // Add more cases for other options as needed
////        }
////    }
//
//    public void performSearch(String value) {
//        filteredList.clear();
//        String searchValue = value.toLowerCase();
//
//        try {
//            if (searchValue.isEmpty()) {
//                filteredList.addAll(requestMoneyModelList);
//                recyclerView.setVisibility(View.VISIBLE);
//                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//                requestMoneyAdapter = new RequestMoneyAdapter(filteredList, getContext());
//                recyclerView.setAdapter(requestMoneyAdapter);
//            } else {
//                for (RequestMoneyModel requestMoneyModel : requestMoneyModelList) {
//                    String label = requestMoneyModel.getRequestLabelTV().toLowerCase();
//                    String headsname = requestMoneyModel.getHeadsTV().toLowerCase();
//                    if (label.contains(searchValue) || headsname.contains(searchValue)) {
//                        filteredList.add(requestMoneyModel);
//                    }
//                }
//
//                if (filteredList.size() > 0) {
//                    recyclerView.setVisibility(View.VISIBLE);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//                    requestMoneyAdapter = new RequestMoneyAdapter(filteredList, getContext());
//                    recyclerView.setAdapter(requestMoneyAdapter);
//                } else {
//                    recyclerView.setVisibility(View.GONE);
////                        no_data.setVisibility(View.VISIBLE);
//                }
//
//                requestMoneyAdapter.setFilter(filteredList);
//            }
//
//            requestMoneyAdapter.setOnItemClickListener(new RequestMoneyAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(RequestMoneyModel model) {
//                    exp_id = model.getId(); // Assuming there is a method to get id from RequestMoneyModel
//                    Intent intent = new Intent(getContext(), RequestDetailsActivity.class);
//                    intent.putExtra("exp_id", exp_id);
//                    startActivity(intent);
//                }
//            });
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    VolleyResponse vr = new VolleyResponse() {
//        @Override
//        public void onResponse(JSONObject obj) throws Exception {
//            Utils.dismisProgressDialog();
//        }
//
//        @Override
//        public void onResponse2(String url_type, JSONObject json) throws Exception {
//            Utils.dismisProgressDialog();
//            Log.d("jsonobject", json.toString());
//            try {
//                if (url_type.equals("expense_view")) {
//                    if (json != null) {
//                        boolean status = json.getBoolean("status");
////                        Toast.makeText(getContext(), "" + json.getString("msg"), Toast.LENGTH_SHORT).show();
//                        if (status) {
//                            try {
//                                JSONArray expenseArray = json.getJSONArray("expense");
//                                requestMoneyModelList.clear();
//
//                                for (int i = 0; i < expenseArray.length(); i++) {
//                                    try {
//                                        JSONObject object = expenseArray.getJSONObject(i);
//                                        String id = object.getString("id");
//                                        String requestLabel = object.getString("label_name");
//                                        String headsname = object.getString("heads_name");
//                                        String amount = object.getString("mrp");
//                                        String paymentStatus = object.getString("status");
//                                        String image = object.getString("image");
//                                        String date = object.getString("date_added");
//
//                                        RequestMoneyModel requestMoneyModel = new RequestMoneyModel(requestLabel, headsname, amount, paymentStatus, date, image, id);
//                                        requestMoneyModelList.add(requestMoneyModel);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    requestMoneyAdapter.notifyDataSetChanged();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        // Show no_data TextView if there is no data
//                        if (requestMoneyModelList.size() == 0) {
//                            no_data.setVisibility(View.VISIBLE);
//                        } else {
//                            no_data.setVisibility(View.GONE);
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    public void getExpenseView() {
//        Utils.showProgressDialog(getContext(), false);
//        HashMap<String, String> params = new HashMap<>();
//        params.put("type", "expense_view");
//        apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
//    }
//
//}
