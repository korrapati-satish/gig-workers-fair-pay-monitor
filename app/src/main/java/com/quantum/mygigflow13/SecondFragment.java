package com.quantum.mygigflow13;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.quantum.mygigflow13.databinding.FragmentSecondBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // File picker result handling
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        readCsvFromUri(uri);
                    }
                });

        // Navigate to first fragment
//        binding.buttonSecond.setOnClickListener(v ->
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
//        );

        // Launch file picker on click
        binding.registerButton.setOnClickListener(v ->

                {
                    Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
                }


        );
    }

    private void readCsvFromUri(Uri uri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder csvContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                csvContent.append(line).append("\n");
            }

            Toast.makeText(getContext(), "CSV Read:\n" + csvContent.toString(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(getContext(), "Error reading file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}