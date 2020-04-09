package com.project.dktpanicbutton.api;

import com.project.dktpanicbutton.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChangePasswordAPI {
    
	@FormUrlEncoded
    @POST("androidapi/changepassword")
    Call<Value> change(@Field("idUser") String idUser,
                       @Field("passwordLama") String passwordLama,
                       @Field("password") String password);
}
