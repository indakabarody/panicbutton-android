package com.project.dktpanicbutton.api;

import com.project.dktpanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SendMessageAPI {

    @FormUrlEncoded
    @POST("androidapi/sendmessage")
    Call<Value> sendMessage(@Field("idAlarm") String idAlarm,
                            @Field("idUser") String idUser,
                            @Field("pesanKhusus") String pesanKhusus);

}