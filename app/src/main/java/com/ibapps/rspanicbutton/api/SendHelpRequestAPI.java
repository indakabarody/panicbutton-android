package com.ibapps.rspanicbutton.api;

import com.ibapps.rspanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SendHelpRequestAPI {
    
	@FormUrlEncoded
    @POST("androidapi/sendhelprequest")
    Call<Value> sendRequest(@Field("idUser") String idUser,
                            @Field("keluhan") String keluhan,
                            @Field("latitude") String latitude,
                            @Field("longitude") String longitude);
}
