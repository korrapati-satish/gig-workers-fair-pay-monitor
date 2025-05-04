package com.quantum.mygigflow13;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.quantum.mygigflow13.model.Message;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView chatRecyclerView;
    private EditText inputMessage;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        inputMessage = view.findViewById(R.id.inputMessage);
        sendButton = view.findViewById(R.id.sendButton);

        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String messageText = inputMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                addMessage(new Message(messageText, true));
                inputMessage.setText("");

                // TODO: Send post request and get response

                // Simulate bot response
//                new Handler().postDelayed(() ->
//                        addMessage(new Message("Echo: " + messageText, false)), 5000);

                // Perform network operation in a background thread
                new Thread(() -> {
                    try {
                        URL url = new URL("https://httpbin.org/post");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        conn.setRequestProperty("Content-Type", "application/json");

                        // JSON payload
                        String json = "{\"message\": \"" + messageText + "\"}";

                        OutputStream os = conn.getOutputStream();
                        os.write(json.getBytes("utf-8"));
                        os.close();

                        Scanner scanner = new Scanner(conn.getInputStream());
                        StringBuilder response = new StringBuilder();
                        while (scanner.hasNext()) {
                            response.append(scanner.nextLine());
                        }
                        scanner.close();

                        // Show response in UI thread
                        getActivity().runOnUiThread(() -> {
                            addMessage(new Message("Bot: " + response.toString(), false));
                        });

                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            addMessage(new Message("Error: " + e.getMessage(), false));
                        });
                    }
                }).start();
            }
        });

        return view;
    }

    private void addMessage(Message message) {
        messageList.add(message);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }
}