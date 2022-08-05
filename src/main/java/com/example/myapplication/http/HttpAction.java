package com.example.myapplication.http;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import com.example.myapplication.model.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpAction extends ConstURL {

    ArrayList<Client> clients = new ArrayList<>();
    Spanned message;
    private static final String SERVER_LOST = "<h1>Server status: offline</h1>";

    public String postEmptyJSON(String url) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder().post(formBody).url(url).build();
        return doResponse(request);
    }

    public String getJSON(String url) throws IOException {
        Request request = new Request.Builder().get().url(url).build();
        return doResponse(request);
    }

    private String doResponse(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public Spanned pingServer() {
        try {
            String json = getJSON(URL_PING);
            this.message = parceHtml(json);
        } catch (IOException e) {
            this.message = parceHtml(SERVER_LOST);
        }
        return this.message;
    }

    public Spanned helpServer() {
        try {
            String json = getJSON(URL_HELP);
            this.message = parceHtml(json);
        } catch (IOException e) {
            this.message = parceHtml(SERVER_LOST);
        }
        return this.message;
    }

    public ArrayList<Client> fetchClients() {
        try {
            String json = postEmptyJSON(URL_CLIEN_LIST);
            parserClients(new JSONObject(json));
        } catch (IOException | JSONException ignored) {
        }
        return this.clients;
    }

    private Spanned parceHtml(String json) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ?
                Html.fromHtml(json, Html.FROM_HTML_MODE_COMPACT) :
                Html.fromHtml(json);
    }

    private void parserClients(JSONObject jsonObject) throws JSONException {
        JSONArray clientJSON = jsonObject.getJSONArray("clientlist");
        for (int i = 0; i < clientJSON.length(); i++) {
            JSONObject object = clientJSON.getJSONObject(i);
            clients.add(new Client(object.getString("ClientName"),
                    object.getString("FullName"),
                    object.getString("Status")));
        }
    }
}
