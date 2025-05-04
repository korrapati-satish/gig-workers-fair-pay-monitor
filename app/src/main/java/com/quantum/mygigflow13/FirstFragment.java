package com.quantum.mygigflow13;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

               sendRequestLoadResults()

//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        binding.chatFirst.setOnClickListener(v -> {
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_ThirdFragment);
                }
        );
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
        String[][] tableData = new String[result.size()][2];

        for (int i = 0; i < result.size(); i++) {
            String row = result.get(i).replaceAll("[{} ]", ""); // remove braces and spaces
            String[] parts = row.split(",");
            tableData[i][0] = parts[0];
            tableData[i][1] = parts[1];
        }

        // Loop through the data and add it to the TableLayout
        for (String[] rowData : tableData) {
            // Create a new TableRow for each row
            TableRow row = new TableRow(getContext());

            // Loop through each column in the row
            for (String cellData : rowData) {
                TextView cell = new TextView(getContext());
                cell.setText(cellData);

                // Set padding (8dp converted to pixels)
                int paddingInDp = 8;
                float scale = getResources().getDisplayMetrics().density;
                int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                cell.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

                // Optional: match the XML styling
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