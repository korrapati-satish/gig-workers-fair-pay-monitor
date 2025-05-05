package com.quantum.mygigflow13;

import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
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
import com.quantum.mygigflow13.databinding.FragmentFirstBinding;
import com.quantum.mygigflow13.model.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
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
                    Toast.makeText(requireContext(), "Processing your data", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    showWeeklyForecastLineGraph();

                    showWeeklyForecastBarGraph();

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
    }

    private void showWeeklyForecastBarGraph() {

        View view =  binding.getRoot();

        BarChart barChart = view.findViewById(R.id.barChart);

// Prepare data
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1f, 4852.01f)); // Week 1
        barEntries.add(new BarEntry(2f, 4873.44f)); // Week 2
        barEntries.add(new BarEntry(3f, 4627.27f)); // Week 3
        barEntries.add(new BarEntry(4f, 4508.33f)); // Week 4
        barEntries.add(new BarEntry(5f, 4750.58f)); // Week 5

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
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "Week " + (int) value;
            }
        });

// Configure Y Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(500f);
        barChart.getAxisRight().setEnabled(false); // Disable right Y-axis

    }

    private void showWeeklyForecastLineGraph() {

        View view =  binding.getRoot();

        LineChart lineChart = view.findViewById(R.id.lineChart);
        if (lineChart == null) {
            Log.e("LineChartFragment", "LineChart view not found!");
            return;
        }

       /* // 1. Create data entries
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 2f));
        entries.add(new Entry(1f, 4f));
        entries.add(new Entry(2f, 1f));
        entries.add(new Entry(3f, 7f));
        entries.add(new Entry(4f, 3f));

        // 2. Create dataset
        LineDataSet dataSet = new LineDataSet(entries, "Sample Data");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.RED);  // Optional: set point color
        dataSet.setCircleRadius(4f);        // Optional: set point size

        // 3. Create LineData and set to chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // 4. Customize chart appearance
        lineChart.getDescription().setText("Line Chart Example");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false); // Hide right y-axis

        // 5. Refresh chart
        lineChart.invalidate(); // Refresh the chart with new data*/

        lineChart = view.findViewById(R.id.lineChart);

        // Prepare data for current vs forecast
        ArrayList<Entry> currentData = new ArrayList<>();
        currentData.add(new Entry(1f, 4852.01f)); // Day 1, Current value
        currentData.add(new Entry(2f, 4873.44f)); // Day 2, Current value
        currentData.add(new Entry(3f, 4627.27f)); // Day 3, Current value
        currentData.add(new Entry(4f, 4508.33f)); // Day 4, Current value
        currentData.add(new Entry(5f, 4750.58f)); // Day 3, Current value

        ArrayList<Entry> forecastData = new ArrayList<>();
        forecastData.add(new Entry(1f, 4969.27f)); // Day 1, Forecast value
        forecastData.add(new Entry(2f, 4948.22f)); // Day 2, Forecast value
        forecastData.add(new Entry(3f, 4945.72f)); // Day 3, Forecast value
        forecastData.add(new Entry(4f, 5050.99f)); // Day 4, Forecast value
        forecastData.add(new Entry(5f, 5200.88f)); // Day 3, Current value

        // Create LineDataSet for the current data
        LineDataSet currentLineDataSet = new LineDataSet(currentData, "Current Month Actual Gross Pay");
        currentLineDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        currentLineDataSet.setValueTextColor(getResources().getColor(R.color.colorPrimary));
        currentLineDataSet.setDrawValues(true);
        currentLineDataSet.setLineWidth(2f);

        // Create LineDataSet for the forecast data
        LineDataSet forecastLineDataSet = new LineDataSet(forecastData, " Current Month Forecasted Gross Pay");
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
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "Week " + (int) value; // Format X Axis as Day 1, Day 2, etc.
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
                URL url = new URL("https://httpbin.org/post");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                // JSON payload
                String json = "[\n" +
                        "    { \"current\": [10,11,12,34,56,77] },\n" +
                        "    { \"forecastedGrossPay\": [11,12,13,25,57,78] }\n" +
                        "]";

                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes("utf-8"));
                os.close();

                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                try {
                    // Parse the response
                    JSONObject responseJson = new JSONObject(String.valueOf(response));
                    JSONArray jsonData = responseJson.getJSONArray("json");

                    // Extract the arrays for current and forecasted gross pay
                    JSONArray currentArray = jsonData.getJSONObject(0).getJSONArray("current");
                    JSONArray forecastedArray = jsonData.getJSONObject(1).getJSONArray("forecastedGrossPay");

                    // Prepare the result list to store pairs
                    List<String> result = new ArrayList<>();

                    // Loop through the arrays and combine the values in pairs
                    for (int i = 0; i < Math.min(currentArray.length(), forecastedArray.length()); i++) {
                        int current = currentArray.getInt(i);
                        int forecasted = forecastedArray.getInt(i);
                        result.add("{" + current + ", " + forecasted + "}");
                    }

                    // Print the result
                    System.out.println("Formatted Output:");
                    for (String pair : result) {
                        System.out.println(pair);
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            renderDataInUi(result);
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
               e.printStackTrace();
            }

        }).start();

    }

    private void renderDataInUi(List<String> result) {

        System.out.println(result);

        if (tableLayout.getChildCount() > 1) {
            tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        }

        // Example data to populate the table
        String[][] tableData = new String[result.size()][3];

        for (int i = 0; i < result.size(); i++) {
            String row = result.get(i).replaceAll("[{} ]", ""); // remove braces and spaces
            String[] parts = row.split(",");
            tableData[i][0] = "2025-04-06";
            tableData[i][1] = parts[0];
            tableData[i][2] = parts[1];
        }

        tableData = new String[][]{
                {"2025-04-06", "4852.01", "4969.27"},
                {"2025-04-13", "4873.44", "4948.22"},
                {"2025-04-20", "4627.27", "4945.72"},
                {"2025-04-27", "4508.33", "5050.99"},
                {"2025-05-04", "4750.58", "5200.88"}
        };
        // Loop through the data and add it to the TableLayout
        for (String[] rowData : tableData) {
            // Create a new TableRow for each row
            TableRow row = new TableRow(getContext());

            // Loop through each column in the row
            for (String cellData : rowData) {
                TextView cell = new TextView(getContext());
                cell.setText(cellData);

                // Set text alignment to center and make it bold
                cell.setGravity(Gravity.CENTER);
                cell.setTypeface(null, Typeface.BOLD);

                // Set padding (8dp converted to pixels)
                int paddingInDp = 8;
                float scale = getResources().getDisplayMetrics().density;
                int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                cell.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

                // Match the XML styling: equal column width with weight
                cell.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                row.addView(cell);
            }

            // Add the TableRow to the TableLayout
            tableLayout.addView(row);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}