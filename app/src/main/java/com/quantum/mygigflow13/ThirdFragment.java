package com.quantum.mygigflow13;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quantum.mygigflow13.databinding.FragmentFirstBinding;
import com.quantum.mygigflow13.databinding.FragmentThirdBinding;
import com.quantum.mygigflow13.model.Message;
import com.quantum.mygigflow13.model.PayComparison;
import com.quantum.mygigflow13.model.PayRecord;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

    private FragmentThirdBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentThirdBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toast.makeText(requireContext(), "Loading Forecasted Data Please wait...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.35:8532/user_data_next_week_forcast");
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

                String city = inputPayRecords.get(0).getCity();
                String platform = inputPayRecords.get(0).getPlatform();
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

                // Group by city and store in a map
                Map<String, Map<String, List<PayRecord>>> groupedByCityAndPlatform = payRecordsResp.stream()
                        .collect(Collectors.groupingBy(
                                PayRecord::getCity,
                                Collectors.groupingBy(PayRecord::getPlatform)
                        ));

                List<PayRecord> payRecordsRespByMumbaiCityUber = groupedByCityAndPlatform.get(city).get(platform);
                List<PayRecord> payRecordsRespByMumbaiCityZomato = groupedByCityAndPlatform.get(city).get("Zomato");
                List<PayRecord> payRecordsRespByMumbaiCitySwiggy = groupedByCityAndPlatform.get(city).get("Swiggy");
                List<PayRecord> payRecordsRespByMumbaiCityRapido = groupedByCityAndPlatform.get(city).get("Rapido");

                List<PayRecord> payRecordsRespByDelhiCityUber = groupedByCityAndPlatform.get("Delhi").get(platform);
                List<PayRecord> payRecordsRespByDelhiCityZomato = groupedByCityAndPlatform.get("Delhi").get("Zomato");
                List<PayRecord> payRecordsRespByDelhiCitySwiggy = groupedByCityAndPlatform.get("Delhi").get("Swiggy");
                List<PayRecord> payRecordsRespByDelhiCityRapido = groupedByCityAndPlatform.get("Delhi").get("Rapido");

                // Show response in UI thread
                getActivity().runOnUiThread(() -> {

                    View viewRoot =  binding.getRoot();

                    TextView textCity = viewRoot.findViewById(R.id.textCity1);
                    textCity.setText(city); // dynamically set city name

                    createLineGraphByPlatform(viewRoot.findViewById(R.id.lineChart1), payRecordsRespByMumbaiCityUber, "uber");
                    createLineGraphByPlatform(viewRoot.findViewById(R.id.lineChart2), payRecordsRespByMumbaiCityZomato, "zomato");
                    createLineGraphByPlatform(viewRoot.findViewById(R.id.lineChart3), payRecordsRespByMumbaiCitySwiggy, "swiggy");
                    createLineGraphByPlatform(viewRoot.findViewById(R.id.lineChart4), payRecordsRespByMumbaiCityRapido, "rapido");

                    createLineGraphByPlatform(viewRoot.findViewById(R.id.lineChart5), payRecordsRespByDelhiCityUber, "uber");
                    createLineGraphByPlatform(viewRoot.findViewById(R.id.lineChart6), payRecordsRespByDelhiCityZomato, "zomato");
                    createLineGraphByPlatform(viewRoot.findViewById(R.id.lineChart7), payRecordsRespByDelhiCitySwiggy, "swiggy");
                    createLineGraphByPlatform(viewRoot.findViewById(R.id.lineChart8), payRecordsRespByDelhiCityRapido, "rapido");


                });

            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    e.printStackTrace();
                });
            }
        }).start();

    }

    private void createLineGraphByPlatform(LineChart lineChart, List<PayRecord> payRecordsRespByCity, String platform) {
        if (lineChart == null) {
            Log.e("LineChartFragment", "LineChart view not found!");
            return;
        }

        ArrayList<Entry> expectedData = new ArrayList<>();

        for (int i = 0; i < payRecordsRespByCity.size(); i++) {
            PayRecord payRecord = payRecordsRespByCity.get(i);
            float xValue = i + 1; // Entry x-axis (e.g., Day 1, 2, 3...)
            expectedData.add(new Entry(xValue, (float) payRecord.getGross_pay()));
        }

        // Create LineDataSet for the forecast data
        LineDataSet forecastLineDataSet = new LineDataSet(expectedData, " Forecasted Gross Pay in "+ platform);
        forecastLineDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        forecastLineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));
        forecastLineDataSet.setDrawValues(true);
        forecastLineDataSet.setLineWidth(2f);

        // Create LineData object
        LineData lineData = new LineData(forecastLineDataSet);

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
        for (PayRecord comparison : payRecordsRespByCity) {
            weekLabels.add(comparison.getWeek_start());
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
}