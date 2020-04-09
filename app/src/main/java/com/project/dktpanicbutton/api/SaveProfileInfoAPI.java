package com.project.dktpanicbutton.api;

import com.project.dktpanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SaveProfileInfoAPI {
    
	@FormUrlEncoded
    @POST("androidapi/saveprofileinfo")
    Call<Value> save(@Field("idUser") String idUser,
                     @Field("idUserOld") String idUserOld,
                     @Field("namaUser") String namaUser,
                     @Field("noHP") String noHP);
}
