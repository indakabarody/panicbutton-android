package com.ibapps.rspanicbutton.api;

import com.ibapps.rspanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterAPI {
    
	@FormUrlEncoded
    @POST("androidapi/register")
    Call<Value> register(@Field("idUser") String idUser,
                         @Field("namaUser") String namaUser,
                         @Field("noHP") String noHP,
                         @Field("password") String password);
}
