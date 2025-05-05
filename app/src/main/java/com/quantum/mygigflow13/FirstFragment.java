package com.quantum.mygigflow13;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quantum.mygigflow13.databinding.FragmentFirstBinding;
import com.quantum.mygigflow13.model.PayComparison;
import com.quantum.mygigflow13.model.PayRecord;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private TableLayout tableLayout;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        return binding.getRoot();


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        binding.buttonFirst.setOnClickListener(v ->
                {
                    Toast.makeText(requireContext(), "Sending data to IBM Watsonx API for processing", Toast.LENGTH_SHORT).show();

                    sendRequestLoadResults();

//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
                }

        );

        binding.registerButton.setOnClickListener(v -> {
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                }
        );


        binding.futureButton.setOnClickListener(v -> {
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_ThirdFragment);
                }
        );

    }

    private void showWeeklyExpectedBarGraph(List<PayComparison> payComparisonList) {

        View view =  binding.getRoot();

        BarChart barChart = view.findViewById(R.id.barChart);

// Prepare data
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < payComparisonList.size(); i++) {
            PayComparison comparison = payComparisonList.get(i);
            float weekIndex = i + 1f; // X-axis value (Week 1, Week 2, ...)
            float currentGrossPay = (float) comparison.getCurrentGrossPay(); // Ensure type cast to float
            barEntries.add(new BarEntry(weekIndex, currentGrossPay));
        }

// Create dataset
        BarDataSet barDataSet = new BarDataSet(barEntries, "Weekly Revenue");
        barDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        barDataSet.setValueTextColor(getResources().getColor(R.color.black));
        barDataSet.setValueTextSize(12f);

// Set data
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f); // Width of bars

        barChart.setData(barData);
        barChart.setFitBars(true); // Make the x-axis fit exactly all bars
        barChart.invalidate(); // Refresh chart

// Customize appearance
        barChart.getDescription().setEnabled(false); // Hide description
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

// Configure X Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // one label per week
        xAxis.setDrawGridLines(false);

        List<String> weekLabels = new ArrayList<>();
        for (PayComparison comparison : payComparisonList) {
            weekLabels.add(comparison.getWeekStart());
        }
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value - 1;
                if (index >= 0 && index < weekLabels.size()) {
                    return weekLabels.get(index);
                } else {
                    return "";
                }
            }
        });

// Configure Y Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(500f);
        barChart.getAxisRight().setEnabled(false); // Disable right Y-axis

    }

    private void showWeeklyExpectedLineGraph(List<PayComparison> payComparisonList) {

        View view =  binding.getRoot();

        LineChart lineChart = view.findViewById(R.id.lineChart);
        if (lineChart == null) {
            Log.e("LineChartFragment", "LineChart view not found!");
            return;
        }


        lineChart = view.findViewById(R.id.lineChart);

        ArrayList<Entry> currentData = new ArrayList<>();
        ArrayList<Entry> expectedData = new ArrayList<>();

        for (int i = 0; i < payComparisonList.size(); i++) {
            PayComparison comparison = payComparisonList.get(i);
            float xValue = i + 1; // Entry x-axis (e.g., Day 1, 2, 3...)

            currentData.add(new Entry(xValue, (float) comparison.getCurrentGrossPay()));
            expectedData.add(new Entry(xValue, (float) comparison.getExpectedGrossPay()));
        }

        // Create LineDataSet for the current data
        LineDataSet currentLineDataSet = new LineDataSet(currentData, "Current Month Actual Gross Pay");
        currentLineDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        currentLineDataSet.setValueTextColor(getResources().getColor(R.color.colorPrimary));
        currentLineDataSet.setDrawValues(true);
        currentLineDataSet.setLineWidth(2f);

        // Create LineDataSet for the forecast data
        LineDataSet forecastLineDataSet = new LineDataSet(expectedData, " Current Month Expected Gross Pay");
        forecastLineDataSet.setColor(getResources().getColor(R.color.colorAccent));
        forecastLineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));
        forecastLineDataSet.setDrawValues(true);
        forecastLineDataSet.setLineWidth(2f);

        // Create LineData object
        LineData lineData = new LineData(currentLineDataSet, forecastLineDataSet);

        // Set the data to the chart
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh the chart

        // Customize the chart appearance
        lineChart.getDescription().setEnabled(false);  // Hide the description
        lineChart.setTouchEnabled(true); // Allow touch gestures
        lineChart.setDragEnabled(true); // Allow dragging
        lineChart.setScaleEnabled(true); // Allow zooming

        // Configure X Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Set granularity to 1 to display one data point per unit

        List<String> weekLabels = new ArrayList<>();
        for (PayComparison comparison : payComparisonList) {
            weekLabels.add(comparison.getWeekStart());
        }


        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                //return "Week " + (int) value; // Format X Axis as Day 1, Day 2, etc.

                int index = (int) value - 1;
                if (index >= 0 && index < weekLabels.size()) {
                    return weekLabels.get(index);
                } else {
                    return "";
                }
            }
        });

        // Configure Y Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setGranularity(10f); // Granularity for the left axis
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable right axis
    }

    private void sendRequestLoadResults() {

        View root =  binding.getRoot();

        // Find the TableLayout from the layout
        tableLayout = root.findViewById(R.id.table_layout);

        // TODO: send request to load results
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.35:8532/user_data_comparision_for_provided_week");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                // JSON payload
                String json = " [\n" +
                        "  {\n" +
                        "    \"week_number\": 0,\n" +
                        "    \"city\": \"Mumbai\",\n" +
                        "    \"platform\": \"Uber\",\n" +
                        "    \"week_start\": \"2025-05-04\",\n" +
                        "    \"hours_worked\": 30.9,\n" +
                        "    \"gross_pay\": 4750.58,\n" +
                        "    \"tips\": 520.9,\n" +
                        "    \"platform_fee\": 725.65,\n" +
                        "    \"petrol_price\": 113.25,\n" +
                        "    \"petrol_price_idx\": 1.298,\n" +
                        "    \"cpi\": 153.38,\n" +
                        "    \"holiday_flag\": 1,\n" +
                        "    \"weather_idx_input\": 1.44,\n" +
                        "    \"temperature\": 39.4,\n" +
                        "    \"humidity\": 64.7,\n" +
                        "    \"rainfall\": 42.9\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"week_number\": 1,\n" +
                        "    \"city\": \"Mumbai\",\n" +
                        "    \"platform\": \"Uber\",\n" +
                        "    \"week_start\": \"2025-04-27\",\n" +
                        "    \"hours_worked\": 46.1,\n" +
                        "    \"gross_pay\": 4508.33,\n" +
                        "    \"tips\": 387.94,\n" +
                        "    \"platform_fee\": 926.85,\n" +
                        "    \"petrol_price\": 116.04,\n" +
                        "    \"petrol_price_idx\": 1.292,\n" +
                        "    \"cpi\": 159.88,\n" +
                        "    \"holiday_flag\": 1,\n" +
                        "    \"weather_idx_input\": 1.45,\n" +
                        "    \"temperature\": 38.7,\n" +
                        "    \"humidity\": 60.6,\n" +
                        "    \"rainfall\": 68.7\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"week_number\": 2,\n" +
                        "    \"city\": \"Mumbai\",\n" +
                        "    \"platform\": \"Uber\",\n" +
                        "    \"week_start\": \"2025-04-20\",\n" +
                        "    \"hours_worked\": 33.4,\n" +
                        "    \"gross_pay\": 4627.27,\n" +
                        "    \"tips\": 808.54,\n" +
                        "    \"platform_fee\": 756.28,\n" +
                        "    \"petrol_price\": 112.18,\n" +
                        "    \"petrol_price_idx\": 1.381,\n" +
                        "    \"cpi\": 154.23,\n" +
                        "    \"holiday_flag\": 1,\n" +
                        "    \"weather_idx_input\": 1.09,\n" +
                        "    \"temperature\": 31.1,\n" +
                        "    \"humidity\": 80.5,\n" +
                        "    \"rainfall\": 29.8\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"week_number\": 3,\n" +
                        "    \"city\": \"Mumbai\",\n" +
                        "    \"platform\": \"Uber\",\n" +
                        "    \"week_start\": \"2025-04-13\",\n" +
                        "    \"hours_worked\": 48.1,\n" +
                        "    \"gross_pay\": 4873.44,\n" +
                        "    \"tips\": 885.35,\n" +
                        "    \"platform_fee\": 888.5,\n" +
                        "    \"petrol_price\": 112.4,\n" +
                        "    \"petrol_price_idx\": 1.412,\n" +
                        "    \"cpi\": 157.75,\n" +
                        "    \"holiday_flag\": 1,\n" +
                        "    \"weather_idx_input\": 1.42,\n" +
                        "    \"temperature\": 32.1,\n" +
                        "    \"humidity\": 87.8,\n" +
                        "    \"rainfall\": 92.5\n" +
                        "  }\n" +
                        "]";

                Gson gson = new Gson();
                Type listType = new TypeToken<List<PayRecord>>(){}.getType();
                List<PayRecord> inputPayRecords = gson.fromJson(json, listType);

                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes("utf-8"));
                os.close();

                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();




                Gson gsonResp = new Gson();
                Type listTypeResp = new TypeToken<ArrayList<PayRecord>>() {}.getType();
                List<PayRecord> payRecordsResp = gsonResp.fromJson(String.valueOf(response), listTypeResp);

                List<PayComparison> comparisonList = new ArrayList<>();

                for (int i = 0; i < inputPayRecords.size(); i++) {
                    PayRecord input = inputPayRecords.get(i);
                    PayRecord output = payRecordsResp.get(i);

                    PayComparison pc = new PayComparison(
                            input.getWeek_start(),  // from input
                            input.getCity(),
                            input.getPlatform(),
                            input.getGross_pay(),   // current
                            output.getGross_pay()   // expected
                    );

                    comparisonList.add(pc);
                }


                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        renderDataInUi(comparisonList);
                        showWeeklyExpectedLineGraph(comparisonList);
                        showWeeklyExpectedBarGraph(comparisonList);
                    });
                }

            } catch (Exception e) {
               e.printStackTrace();
            }

        }).start();

    }

    private void renderDataInUi(List<PayComparison> payComparisonList) {

        System.out.println(payComparisonList);

        if (tableLayout.getChildCount() > 1) {
            tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        }

        for (PayComparison comparison : payComparisonList) {
            TableRow row = new TableRow(getContext());

            String[] rowData = new String[]{
                    comparison.getWeekStart(),
                    String.format("%.2f", comparison.getCurrentGrossPay()),
                    String.format("%.2f", comparison.getExpectedGrossPay())
            };

            for (String cellData : rowData) {
                TextView cell = new TextView(getContext());
                cell.setText(cellData);
                cell.setGravity(Gravity.CENTER);
                cell.setTypeface(null, Typeface.BOLD);

                int paddingInDp = 8;
                float scale = getResources().getDisplayMetrics().density;
                int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                cell.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

                cell.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                row.addView(cell);
            }

            tableLayout.addView(row);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}