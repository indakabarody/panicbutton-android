package com.project.dktpanicbutton.api;

import com.project.dktpanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetNotifAPI {
    
	@FormUrlEncoded
    @POST("androidapi/getnotif")
    Call<Value> getNotif(@Field("idAlarm") String idAlarm);
}