package com.asuris19.recorder;

import org.json.JSONObject;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioRecognizerApi {

    public static String transcribe(String fileName) throws Exception {
        String secretKey = "0b8ca1f30a354ffda8446c1e85f59d01";

        OkHttpClient client = new OkHttpClient();

        byte[] data = org.apache.commons.io.FileUtils.readFileToByteArray(new File(fileName));

        RequestBody requestBody = RequestBody.create(data);

        Request request = new Request.Builder()
                .addHeader("Content-type", "application/octet-stream")
                .url("https://api.speechtext.ai/recognize?key=" + secretKey + "&language=ru-RU&punctuation=true")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());
        String task = json.getString("id");

        while (true) {
            request = new Request.Builder()
                    .url("https://api.speechtext.ai/results?key=" + secretKey + "&task=" + task)
                    .get()
                    .build();

            response = client.newCall(request).execute();

            json = new JSONObject(response.body().string());
            String status = json.getString("status");

            if (status.equals("failed"))
                return "[не удалось распознать]";

            if (status.equals("finished"))
                return json.getJSONObject("results").getString("transcript");

            Thread.sleep(1000);
        }
    }
}