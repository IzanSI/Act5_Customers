package com.example.act5_customers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OdooClient {
    @Headers("Content-Type: application/json")
    @POST("/jsonrpc")
    Call<JsonObject> authenticate(@Body JsonObject body);
}
