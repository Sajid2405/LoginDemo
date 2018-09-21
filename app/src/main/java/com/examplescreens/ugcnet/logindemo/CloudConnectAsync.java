package com.examplescreens.ugcnet.logindemo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudConnectAsync extends AsyncTask<Context, Void, String> {

    Context context;

    @Override
    protected String doInBackground(Context... contexts) {
        context = contexts[0];

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", LoginActivity.email)
                .addFormDataPart("password", LoginActivity.password)
                .build();

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("http://159.65.7.75:9000/v1/auth")
                .post(requestBody)
                .build();

        Response response = null;

        try{
            response = okHttpClient.newCall(request).execute();
            return response.body().string();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;


/*
        StringBuilder result = new StringBuilder();
        URL url;
        HttpURLConnection httpURLConnection;
        try{
            url = new URL("https://api.getpostman.com/collections/");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();

            while(data != -1){
                Log.i("MY_APP", String.valueOf(data));
                char dataCh = (char) data;

                result.append(dataCh);
                data = inputStreamReader.read();
            }

        }catch (Exception e){
            Log.i("MY_APP", "InExceptionResult = "+result);
            Log.i("MY_APP", "Exception:"+e.getMessage());
        }
        Log.i("MY_APP", "Result: "+result.toString());
        return result.toString();*/
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.i("MY_APP", "Result: " + s);
    }
}
