package com.ibapps.rspanicbutton.api;

import com.ibapps.rspanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LogoutAPI {
    
	@FormUrlEncoded
    @POST("androidapi/logout")
    Call<Value> logout(@Field("idUser") String idUser);
}
