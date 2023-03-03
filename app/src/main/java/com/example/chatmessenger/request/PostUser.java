package com.example.chatmessenger.request;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class PostUser extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;

        try {
            JsonObject postData = new JsonObject();
            postData.addProperty("login", params[0]);
            postData.addProperty("nickname", params[1]);
            postData.addProperty("phone", params[2]);
            postData.addProperty("email", params[3]);
            postData.addProperty("password", params[4]);
            postData.addProperty("password_md5", params[5]);
            postData.addProperty("is_online", params[6]);

            URL url = new URL("http://mymessenger.ua/api/users");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    out, "UTF-8"
            ));
            writer.write(postData.toString());
            writer.flush();

            int code = urlConnection.getResponseCode();
            if (code != 201) {
                throw new IOException("Invalid response from server: " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
