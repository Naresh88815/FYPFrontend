package com.application.expensemanager.fragments;

import static com.application.expensemanager.utils.MyApplication.apinetwork;
import static com.application.expensemanager.utils.MyApplication.mSp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.expensemanager.R;
import com.application.expensemanager.activity.ReceivedRequestDetailsActivity;
import com.application.expensemanager.activity.RequestDetailsActivity;
import com.application.expensemanager.adapter.ReceivedRequestAdapter;
import com.application.expensemanager.model.ExpenseHeadModel;
import com.application.expensemanager.model.ReceivedRequestModel;
import com.application.expensemanager.model.RequestMoneyModel;
import com.application.expensemanager.network.VolleyResponse;
import com.application.expensemanager.utils.Constants;
import com.application.expensemanager.utils.MyApplication;
import com.application.expensemanager.utils.SPCsnstants;
import com.application.expensemanager.utils.Utils;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReceivedRequest extends Fragment {
    View view;
    RecyclerView recyclerView;
    List<ReceivedRequestModel> receivedRequestModelList = new ArrayList<>();
    List<RequestMoneyModel> requestModelList = new ArrayList<>();
    List<ExpenseHeadModel> headsList = new ArrayList<>();
    EditText searchLabel;
    List<ReceivedRequestModel> filteredList = new ArrayList<>();
    ReceivedRequestAdapter receivedRequestAdapter;
    String paymentStatus, exp_id, amount, label, image, date, requestedBy;
    TextView no_data;
    Spinner sort_spinner, sort_by_date, sort_by_amt;
    String startDateString = "";
    String endDateString = "";
    TextView requestby, date_title;
    LinearLayout heading;
    private NestedScrollView nestedSV;
    private String paginationValue;
    int page = 1;
    boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isFiltterLastPage = false;
    private boolean filterApplied = false;
    private boolean amountFiltter = false;
    private boolean sortByFiltter = false;
    private String currentAmountFiltterType = "";
    private String currentSortFiltterType = "";
    private boolean dateFiltter = false;
    private boolean ascendingDateFiltter = false;
    private boolean fromSearchBar = false;
    private boolean startDateFiltter = false;
    private boolean endDateFiltter = false;
    private boolean isSearchingData = false;
    private String currentDateFiltterType = "";
    private String startDateFiltterType = "";
    private String endDateFiltterType = "";
    private String searchText = "";
    private SimpleDateFormat dateFormatWithoutTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private Calendar startDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
    private Calendar endDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));



    @Override
    public void onResume() {
        super.onResume();
        //getPaginationExpenseView(paginationValue);
        //getExpenseView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.received_request_list, container, false);
        initView();
        if (MyApplication.mSp.getKey(SPCsnstants.super_user).equals("0")) {
            requestby.setVisibility(View.GONE);

        } else {

            requestby.setVisibility(View.VISIBLE);
        }

        paginationValue = Integer.toString(page);
        getPaginationExpenseView(paginationValue);

        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (!isLoading && !isLastPage) {
                        page++;
                        paginationValue = Integer.toString(page);
                        if (isSearchingData) {
                            searchData(paginationValue, searchText);
                        }
                        fromSearchBar = true;
                        getPaginationExpenseView(paginationValue);
                    }
                }
            }
        });

        final Handler handler = new Handler();
        final long delay = 0;
        searchLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.postDelayed(() -> {
                    page = 1;
                    paginationValue = "" + page;
                    receivedRequestModelList.clear();
                    isSearchingData = true;
                    searchText = capitalizeFirstLetter(s.toString());
                    searchData(paginationValue, searchText);

                    if (s.toString().equals("") || s.toString().equals(null)) {
                        isSearchingData = false;
                        getPaginationExpenseView(paginationValue);
                    }
                }, delay);
            }
        });

//        requestMoneyAdapter.setOnItemClickListener(new RequestMoneyAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(RequestMoneyModel model) {
//                exp_id = model.getId(); // Assuming there is a method to get id from RequestMoneyModel
//
//                Intent intent = new Intent(getContext(), RequestDetailsActivity.class);
//                intent.putExtra("exp_id", exp_id);
//                startActivity(intent);
//            }
//        });


        // Set up the ArrayAdapter for the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_by_options,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort_spinner.setAdapter(adapter);
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection based on the selected position
                sortByOptions(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here if needed
            }
        });

//        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
//                getContext(),
//                R.array.sort_by_date,
//                android.R.layout.simple_spinner_item
//        );
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        sort_by_date.setAdapter(adapter1);
//        sort_by_date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                sortByDate(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_by_amt,
                android.R.layout.simple_spinner_item
        );

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort_by_amt.setAdapter(adapter2);
        sort_by_amt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortByAmount(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    public void initView() {
        nestedSV = view.findViewById(R.id.NestedSV);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        no_data = view.findViewById(R.id.no_data);
        searchLabel = view.findViewById(R.id.searchLabel);
        sort_spinner = view.findViewById(R.id.sort_spinner);
        //sort_by_date = view.findViewById(R.id.sort_by_date);
        sort_by_amt = view.findViewById(R.id.sort_by_amt);
        requestby = view.findViewById(R.id.requestby);
        heading = view.findViewById(R.id.heading);
        date_title = view.findViewById(R.id.date_title);
        if (MyApplication.mSp.getKey(SPCsnstants.super_user).equals("0")) {
            heading.setWeightSum(8);
            date_title.setGravity(Gravity.CENTER);
            date_title.setPadding(50, 0, 0, 0);
        } else {
            heading.setWeightSum(9);
            date_title.setGravity(Gravity.CENTER);
            date_title.setPadding(0, 0, 0, 0);
        }

        receivedRequestAdapter = new ReceivedRequestAdapter(receivedRequestModelList, getContext());
        receivedRequestAdapter.setOnItemClickListener(new ReceivedRequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ReceivedRequestModel model) {
                exp_id = model.getExp_id(); // Assuming there is a method to get id from RequestMoneyModel
                if (MyApplication.mSp.getKey(SPCsnstants.super_user).equals("0")) {
                    Intent intent = new Intent(getContext(), RequestDetailsActivity.class);
                    intent.putExtra("exp_id", exp_id);

                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getContext(), ReceivedRequestDetailsActivity.class);
                    intent.putExtra("exp_id", exp_id);
                    startActivity(intent);
                }

            }
        });
        recyclerView.setAdapter(receivedRequestAdapter);
    }

    private void sortByOptions(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                // Sort by "Request By" in alphabetical order
//                Collections.sort(receivedRequestModelList, new Comparator<ReceivedRequestModel>() {
//                    @Override
//                    public int compare(ReceivedRequestModel o1, ReceivedRequestModel o2) {
//                        // Assuming getRequestByTV() is a method in RequestMoneyModel to get "Request By" value
//                        return o1.getRequestByTV().compareToIgnoreCase(o2.getRequestByTV());
//                    }
//                });
//                receivedRequestAdapter.notifyDataSetChanged();
                filterApplied = true;
                sortByFiltter = true;
                dateFiltter = false;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentSortFiltterType = "request_by";
                getPaginationExpenseView(paginationValue);
                break;
            case 2:
//                Collections.sort(receivedRequestModelList, new Comparator<ReceivedRequestModel>() {
//                    @Override
//                    public int compare(ReceivedRequestModel o1, ReceivedRequestModel o2) {
//                        return o1.getStatusTV().compareToIgnoreCase(o2.getStatusTV());
//                    }
//                });
//                receivedRequestAdapter.notifyDataSetChanged();
                filterApplied = true;
                sortByFiltter = true;
                dateFiltter = false;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentSortFiltterType = "status";
                getPaginationExpenseView(paginationValue);
                break;
            case 3:
//                Collections.sort(receivedRequestModelList, new Comparator<ReceivedRequestModel>() {
//                    @Override
//                    public int compare(ReceivedRequestModel o1, ReceivedRequestModel o2) {
//                        return o1.getHeadsTV().compareToIgnoreCase(o2.getHeadsTV());
//                    }
//                });
//                receivedRequestAdapter.notifyDataSetChanged();
                filterApplied = true;
                sortByFiltter = true;
                dateFiltter = false;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentSortFiltterType = "exp_heads";
                getPaginationExpenseView(paginationValue);
                break;

            case 4:
                showDateTimePicker(true);
                break;

            case 5:
                filterApplied = true;
                startDateFiltter = false;
                endDateFiltter = false;
                sortByFiltter = false;
                dateFiltter = true;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentDateFiltterType = "asc";
                getPaginationExpenseView(paginationValue);
                break;

            case 6:
                filterApplied = true;
                startDateFiltter = false;
                endDateFiltter = false;
                sortByFiltter = false;
                dateFiltter = true;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentDateFiltterType = "desc";
                getPaginationExpenseView(paginationValue);
                break;

        }
    }

    private void sortByAmount(int position) {
        // Implement logic based on the selected position
        switch (position) {

            case 1:
//                Collections.sort(receivedRequestModelList, new Comparator<ReceivedRequestModel>() {
//                    @Override
//                    public int compare(ReceivedRequestModel o1, ReceivedRequestModel o2) {
//                        // Convert string amounts to double for proper comparison
//                        double amount1 = Double.parseDouble(o1.getAmountTV());
//                        double amount2 = Double.parseDouble(o2.getAmountTV());
//                        // Use Double.compare for proper comparison of double values
//                        return Double.compare(amount1, amount2);
//                    }
//                });
                filterApplied = true;
                amountFiltter = true;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentAmountFiltterType = "low_to_high";
                getPaginationExpenseView(paginationValue);
                //receivedRequestAdapter.notifyDataSetChanged();
                break;
            case 2:
//                Collections.sort(receivedRequestModelList, new Comparator<ReceivedRequestModel>() {
//                    @Override
//                    public int compare(ReceivedRequestModel o1, ReceivedRequestModel o2) {
//                        // Convert string amounts to double for proper comparison
//                        double amount1 = Double.parseDouble(o1.getAmountTV());
//                        double amount2 = Double.parseDouble(o2.getAmountTV());
//                        // Use Double.compare for proper comparison of double values
//                        return Double.compare(amount2, amount1);
//                    }
//                });
                filterApplied = true;
                amountFiltter = true;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentAmountFiltterType = "high_to_low";
                getPaginationExpenseView(paginationValue);
                //receivedRequestAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void sortByDate(int position) {
        // Implement logic based on the selected position
        switch (position) {

            case 1:
                //DatePickerDialog();
                showDateTimePicker(true);
                break;

            case 2:
//                Collections.sort(receivedRequestModelList, new Comparator<ReceivedRequestModel>() {
//                    @Override
//                    public int compare(ReceivedRequestModel o1, ReceivedRequestModel o2) {
//                        return o1.getDateTV().compareToIgnoreCase(o2.getDateTV());
//                    }
//                });
//                receivedRequestAdapter.notifyDataSetChanged();
                filterApplied = true;
                startDateFiltter = false;
                endDateFiltter = false;
                dateFiltter = true;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentDateFiltterType = "asc";
                getPaginationExpenseView(paginationValue);
                break;
            case 3:
//                Collections.sort(receivedRequestModelList, new Comparator<ReceivedRequestModel>() {
//                    @Override
//                    public int compare(ReceivedRequestModel o1, ReceivedRequestModel o2) {
//                        return o2.getDateTV().compareToIgnoreCase(o1.getDateTV());
//                    }
//                });
//                receivedRequestAdapter.notifyDataSetChanged();
                filterApplied = true;
                startDateFiltter = false;
                endDateFiltter = false;
                dateFiltter = true;
                isFiltterLastPage = false;
                page = 1;
                paginationValue = "" + page;
                receivedRequestModelList.clear();
                currentDateFiltterType = "desc";
                getPaginationExpenseView(paginationValue);
                break;
        }
    }

    private void DatePickerDialog() {
        // Creating a MaterialDatePicker builder for selecting a date range
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        // Building the date picker dialog
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {

            // Retrieving the selected start and end dates
            Long startDate = selection.first;
            Long endDate = selection.second;

            // Formating the selected dates as strings
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            startDateString = sdf.format(new Date(startDate));
            endDateString = sdf.format(new Date(endDate));

            // Creating the date range string
//            String selectedDateRange = startDateString + " - " + endDateString;
//            Toast.makeText(getContext(), ""+selectedDateRange, Toast.LENGTH_SHORT).show();
            // Filter the data based on the selected date range
            filterDataByDateRange(startDateString, endDateString);
            receivedRequestAdapter.notifyDataSetChanged();

        });

        // Showing the date picker dialog
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

    }

    private void filterDataByDateRange(String startDate, String endDate) {
        List<ReceivedRequestModel> filteredList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date startDateFormatted = sdf.parse(startDate + " 00:00:00");
            Date endDateFormatted = sdf.parse(endDate + " 23:59:59");

            for (ReceivedRequestModel model : receivedRequestModelList) {
                Date modelDate = sdf.parse(model.getDateTV());

                if (modelDate != null && (modelDate.after(startDateFormatted) || modelDate.equals(startDateFormatted)) &&
                        (modelDate.before(endDateFormatted) || modelDate.equals(endDateFormatted))) {
                    filteredList.add(model);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (filteredList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            receivedRequestAdapter = new ReceivedRequestAdapter(filteredList, getContext());
            recyclerView.setAdapter(receivedRequestAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            // Handle the case when there is no data in the selected date range
            // no_data.setVisibility(View.VISIBLE);
        }
    }

    public void performSearch(String value) {
        filteredList.clear();
        String searchValue = value.toLowerCase();

        try {
            if (searchValue.isEmpty()) {
                // If the search query is empty, show all initial values
                filteredList.addAll(receivedRequestModelList);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                receivedRequestAdapter = new ReceivedRequestAdapter(receivedRequestModelList, getContext());
                recyclerView.setAdapter(receivedRequestAdapter);
            } else {
                for (ReceivedRequestModel receivedRequestModel : receivedRequestModelList) {
                    String heads = receivedRequestModel.getHeadsTV().toLowerCase(); // Convert label to lowercase
                    String label = receivedRequestModel.getRequestLabelTV().toLowerCase(); // Convert label to lowercase
                    String employeeName = receivedRequestModel.getRequestByTV().toLowerCase();
                    if (heads.contains(searchValue) || label.contains(searchValue) || employeeName.contains(searchValue)) {
                        filteredList.add(receivedRequestModel);
                    }
                }

                if (filteredList.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    receivedRequestAdapter = new ReceivedRequestAdapter(filteredList, getContext());
                    recyclerView.setAdapter(receivedRequestAdapter);

                } else {
                    recyclerView.setVisibility(View.GONE);
//                        no_data.setVisibility(View.VISIBLE);
                }

                receivedRequestAdapter.setFilter(filteredList);
            }
            receivedRequestAdapter.setOnItemClickListener(new ReceivedRequestAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ReceivedRequestModel model) {
                    exp_id = model.getExp_id(); // Assuming there is a method to get id from RequestMoneyModel

                    Intent intent = new Intent(getContext(), ReceivedRequestDetailsActivity.class);
                    intent.putExtra("exp_id", exp_id);
                    startActivity(intent);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    VolleyResponse vr = new VolleyResponse() {
        @Override
        public void onResponse(JSONObject obj) throws Exception {
            Utils.dismisProgressDialog();
        }

        @Override
        public void onResponse2(String url_type, JSONObject json) throws Exception {
            isLoading = false;
            Utils.dismisProgressDialog();
            try {
                if (url_type.equals("expense_view")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");

                        if (status) {
                            JSONArray expenseArray = json.getJSONArray("expenses");

                            Log.d("expenses", String.valueOf(expenseArray));

                            for (int i = 0; i < expenseArray.length(); i++) {
                                try {
                                    JSONObject object = expenseArray.getJSONObject(i);
                                    String exp_id = object.getString("expense_id");
                                    String userName = object.getString("user_name");
                                    String requestLabel = object.getString("label_name");
                                    String headsname = object.getString("heads_name");
                                    String amount = object.getString("amount");
                                    String paymentStatus = object.getString("status");
                                    String date = object.getString("date_added");
                                    String image = object.getString("image");
                                    ReceivedRequestModel receivedRequestModel = new ReceivedRequestModel(exp_id, userName, requestLabel, headsname, amount, paymentStatus, date, image);
                                    receivedRequestModelList.add(receivedRequestModel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            receivedRequestAdapter.notifyDataSetChanged();

                            // Check if it's the last page
                            int totalPages = json.getInt("total_pages");
                            if (!filterApplied) {
                                if (page >= totalPages) {
                                    isLastPage = true;
                                }
                            } else if (filterApplied) {
                                if (page >= totalPages) {
                                    isFiltterLastPage = true;
                                }
                            }


                        }
                        // Show no_data TextView if there is no data
                        if (receivedRequestModelList.size() == 0) {
                            no_data.setVisibility(View.VISIBLE);
                        } else {
                            no_data.setVisibility(View.GONE);
                        }
                    }
                } else if (url_type.equals("expense_search")) {
                    if (json != null) {
                        boolean status = json.getBoolean("status");

                        if (status) {
                            JSONArray expenseArray = json.getJSONArray("expense");

                            Log.d("expenses", String.valueOf(expenseArray));

                            for (int i = 0; i < expenseArray.length(); i++) {
                                try {
                                    JSONObject object = expenseArray.getJSONObject(i);
                                    String exp_id = object.getString("id");
                                    String userName = object.getString("user_name");
                                    String requestLabel = object.getString("label_name");
                                    String headsname = object.getString("heads_name");
                                    String amount = object.getString("mrp");
                                    String paymentStatus = object.getString("status");
                                    String date = object.getString("date_added");
                                    String image = object.getString("image");
                                    boolean image_state = object.getBoolean("image_status");

                                    ReceivedRequestModel receivedRequestModel = new ReceivedRequestModel(exp_id, userName, requestLabel, headsname, amount, paymentStatus, date, image);
                                    receivedRequestModelList.add(receivedRequestModel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            receivedRequestAdapter.notifyDataSetChanged();

                            // Check if it's the last page
//                            int totalPages = json.getInt("total_pages");
//                            if (!filterApplied) {
//                                if (page >= totalPages) {
//                                    isLastPage = true;
//                                }
//                            } else if (filterApplied) {
//                                if (page >= totalPages) {
//                                    isFiltterLastPage = true;
//                                }
//                            }


                        }
                        // Show no_data TextView if there is no data
                        if (receivedRequestModelList.size() == 0) {
                            no_data.setVisibility(View.VISIBLE);
                        } else {
                            no_data.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private void searchData(String page, String searchText) {
        //Utils.showProgressDialog(getContext(), false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "expense_search");
        params.put("page", paginationValue);
        params.put("search", searchText);
        apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
    }

    private void getPaginationExpenseView(String page) {
        if (!isLastPage && !filterApplied) {
            Utils.showProgressDialog(getContext(), false);
            isLoading = true;
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "expense_view");
            params.put("page", page);
            params.put("emp_id", mSp.getKey(SPCsnstants.id));
            apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
        } else if (filterApplied && !isFiltterLastPage && amountFiltter && !sortByFiltter && !dateFiltter) {
            Utils.showProgressDialog(getContext(), false);
            isLoading = true;
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "expense_view");
            params.put("page", page);
            params.put("amount_filter", currentAmountFiltterType);
            apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
        } else if (filterApplied && !isFiltterLastPage && !amountFiltter && sortByFiltter && !dateFiltter) {
            Utils.showProgressDialog(getContext(), false);
            isLoading = true;
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "expense_view");
            params.put("page", page);
            params.put("sort_filter", currentSortFiltterType);
            apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
        } else if (filterApplied && !isFiltterLastPage && amountFiltter && sortByFiltter && !dateFiltter) {
            Utils.showProgressDialog(getContext(), false);
            isLoading = true;
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "expense_view");
            params.put("page", page);
            params.put("amount_filter", currentAmountFiltterType);
            params.put("sort_filter", currentSortFiltterType);
            apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
        } else if (filterApplied && !isFiltterLastPage && !amountFiltter && !sortByFiltter && dateFiltter) {
            if (!startDateFiltter && !endDateFiltter) {
                Utils.showProgressDialog(getContext(), false);
                isLoading = true;
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "expense_view");
                params.put("page", page);
                params.put("datefilter", currentDateFiltterType);
                apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
            } else {
                Utils.showProgressDialog(getContext(), false);
                isLoading = true;
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "expense_view");
                params.put("page", page);
                params.put("datefilter", "date");
                params.put("start_date", startDateFiltterType);
                params.put("end_date", endDateFiltterType);
                apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
            }
        } else if (filterApplied && !isFiltterLastPage && amountFiltter && !sortByFiltter && dateFiltter) {
            if (!startDateFiltter && !endDateFiltter) {
                Utils.showProgressDialog(getContext(), false);
                isLoading = true;
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "expense_view");
                params.put("page", page);
                params.put("amount_filter", currentAmountFiltterType);
                params.put("datefilter", currentDateFiltterType);
                apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
            } else {
                Utils.showProgressDialog(getContext(), false);
                isLoading = true;
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "expense_view");
                params.put("page", page);
                params.put("amount_filter", currentAmountFiltterType);
                params.put("datefilter", "date");
                params.put("start_date", startDateFiltterType);
                params.put("end_date", endDateFiltterType);
                apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
            }
        }else if (filterApplied && !isFiltterLastPage && !amountFiltter && sortByFiltter && dateFiltter) {
            if (!startDateFiltter && !endDateFiltter) {
                Utils.showProgressDialog(getContext(), false);
                isLoading = true;
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "expense_view");
                params.put("page", page);
                params.put("sort_filter", currentSortFiltterType);
                params.put("datefilter", currentDateFiltterType);
                apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
            } else {
                Utils.showProgressDialog(getContext(), false);
                isLoading = true;
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "expense_view");
                params.put("page", page);
                params.put("sort_filter", currentSortFiltterType);
                params.put("datefilter", "date");
                params.put("start_date", startDateFiltterType);
                params.put("end_date", endDateFiltterType);
                apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
            }
        }else if (filterApplied && !isFiltterLastPage && amountFiltter && sortByFiltter && dateFiltter) {
            if (!startDateFiltter && !endDateFiltter) {
                Utils.showProgressDialog(getContext(), false);
                isLoading = true;
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "expense_view");
                params.put("page", page);
                params.put("amount_filter", currentAmountFiltterType);
                params.put("sort_filter", currentSortFiltterType);
                params.put("datefilter", currentDateFiltterType);
                apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
            } else {
                Utils.showProgressDialog(getContext(), false);
                isLoading = true;
                HashMap<String, String> params = new HashMap<>();
                params.put("type", "expense_view");
                params.put("page", page);
                params.put("amount_filter", currentAmountFiltterType);
                params.put("sort_filter", currentSortFiltterType);
                params.put("datefilter", "date");
                params.put("start_date", startDateFiltterType);
                params.put("end_date", endDateFiltterType);
                apinetwork.requestWithJsonObject(Constants.VIEW_EXPENSE, params, vr, "expense_view");
            }
        }

    }


    private void showDateTimePicker(final boolean isStartDate) {
        final Calendar startCalendar = startDateCalendar;
        final Calendar endCalendar = endDateCalendar;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (isStartDate) {
                            startCalendar.set(Calendar.YEAR, year);
                            startCalendar.set(Calendar.MONTH, monthOfYear);
                            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            // Check if start date is greater than end date, swap if needed
                            if (startCalendar.after(endCalendar)) {
                                // Swap start and end dates
                                Calendar tempCalendar = (Calendar) startCalendar.clone();
                                startCalendar.setTime(endCalendar.getTime());
                                endCalendar.setTime(tempCalendar.getTime());
                            }
                        } else {
                            endCalendar.set(Calendar.YEAR, year);
                            endCalendar.set(Calendar.MONTH, monthOfYear);
                            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            // Check if end date is earlier than start date, swap if needed
                            if (endCalendar.before(startCalendar)) {
                                // Swap start and end dates
                                Calendar tempCalendar = (Calendar) endCalendar.clone();
                                endCalendar.setTime(startCalendar.getTime());
                                startCalendar.setTime(tempCalendar.getTime());
                            }
                        }

                        showTimePicker(isStartDate);
                    }
                },
                startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set a custom title for the DatePickerDialog
        String title = isStartDate ? "Start Date" : "End Date";
        datePickerDialog.setTitle(title);

        datePickerDialog.show();
    }

    private void showTimePicker(final boolean isStartDate) {
        final Calendar currentCalendar;
        if (isStartDate) {
            currentCalendar = startDateCalendar;
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
            currentCalendar.set(Calendar.MINUTE, 0);
            showDateTimePicker(false);
            return;
        } else {
            currentCalendar = endDateCalendar;
            currentCalendar.set(Calendar.HOUR_OF_DAY, 23);
            currentCalendar.set(Calendar.MINUTE, 59);
            updateDateText();
        }
    }
    private void updateDateText() {
        startDateFiltterType = dateFormatWithoutTime.format(startDateCalendar.getTime());
        endDateFiltterType = dateFormatWithoutTime.format(endDateCalendar.getTime());

        //Toast.makeText(getContext(), "Start Date: " + startDateFiltterType + "\nEnd Date: " + endDateFiltterType, Toast.LENGTH_LONG).show();

        if (!startDateFiltterType.isEmpty() && !endDateFiltterType.isEmpty()) {
            filterApplied = true;
            startDateFiltter = true;
            endDateFiltter = true;
            dateFiltter = true;
            sortByFiltter = false;
            isFiltterLastPage = false;
            page = 1;
            paginationValue = "" + page;
            receivedRequestModelList.clear();
            getPaginationExpenseView(paginationValue);
        }
    }
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}

