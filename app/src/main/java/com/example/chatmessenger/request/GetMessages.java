package com.example.chatmessenger.request;

import android.os.AsyncTask;

import com.example.chatmessenger.chat_message.ChatMessage;
import com.example.chatmessenger.chat_user.ChatUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class GetMessages extends AsyncTask<Void, Void, Void> {

    private ArrayList<ChatMessage> messages;

    public GetMessages() {
        this.messages = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        String jsonString = null;

        try {
            URL url = new URL("http://mymessenger.ua/api/messages");
            urlConnection = (HttpsURLConnection) url.openConnection();

            int code = urlConnection.getResponseCode();
            if (code != 200) {
                throw new IOException("Invalid response from server: " + code);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()
            ));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            jsonString = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        if (jsonString != null) {
            Gson gson = new Gson();
            Type arrayListType = new TypeToken<ArrayList<ChatMessage>>() {}.getType();
            messages = gson.fromJson(jsonString, arrayListType);
        } else {
            return null;
        }
        return null;
    }

    public ArrayList<ChatMessage> retrieveMessages() {
        return this.messages;
    }
}
