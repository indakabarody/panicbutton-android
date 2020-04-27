package com.ibapps.rspanicbutton.api;

import com.ibapps.rspanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginAPI {
    
	@FormUrlEncoded
    @POST("androidapi/login")
    Call<Value> login(@Field("idUser") String idUser,
                      @Field("password") String password);
}
