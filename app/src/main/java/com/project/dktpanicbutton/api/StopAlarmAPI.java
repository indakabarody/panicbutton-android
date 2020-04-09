package com.project.dktpanicbutton.api;

import com.project.dktpanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface StopAlarmAPI {
    
	@FormUrlEncoded
    @POST("androidapi/stopalarm")
    Call<Value> stopAlarm(@Field("idAlarm") String idAlarm);
}