package com.example.act5_customers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class OdooLoginService {

    private final String baseUrl;
    private final String db;

    public interface SuccessCallback {
        void onSuccess(int uid);
    }

    public interface ErrorCallback {
        void onError(String message);
    }

    public OdooLoginService(String baseUrl, String db) {
        this.baseUrl = baseUrl;
        this.db = db;
    }

    public void login(String username, String password, SuccessCallback onSuccess, ErrorCallback onError) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OdooClient client = retrofit.create(OdooClient.class);

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(username);
        args.add(password);

        JsonObject params = new JsonObject();
        params.addProperty("service", "common");
        params.addProperty("method", "login");
        params.add("args", args);

        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");
        body.add("params", params);
        body.addProperty("id", 1);

        Log.d("LOGIN_REQUEST", body.toString()); // log del JSON enviado

        Call<JsonObject> call = client.authenticate(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject responseBody = response.body();
                    if (responseBody == null) {
                        onError.onError("Respuesta vacía del servidor");
                        return;
                    }

                    Log.d("LOGIN_RESPONSE", responseBody.toString());

                    if (responseBody.has("result") && responseBody.get("result").isJsonPrimitive()) {
                        int uid = responseBody.get("result").getAsInt();  // 👈 Aquí ya NO se trata como JsonObject
                        onSuccess.onSuccess(uid);
                    } else if (responseBody.has("error")) {
                        JsonObject error = responseBody.getAsJsonObject("error");
                        String message = error.has("message") ? error.get("message").getAsString() : "Error desconocido";
                        onError.onError("Error desde Odoo: " + message);
                    } else {
                        onError.onError("Respuesta inesperada del servidor");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onError.onError("Excepción: " + e.toString());
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                onError.onError("Fallo de conexión: " + t.getMessage());
            }
        });
    }
}
