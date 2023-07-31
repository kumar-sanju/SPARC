package com.smart.sparc;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("auth/signUp")
    Call<ApiResponse> registerUser(
            @Part("name") RequestBody name,
            @Part("userName") RequestBody userName,
            @Part("password") RequestBody password,
            @Part("email") RequestBody email,
            @Part("address") RequestBody address,
            @Part("age") RequestBody age,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part("roleId") RequestBody roleId,
//            @Part("img") MultipartBody.Part image
            @Part MultipartBody.Part image
    );

    @POST("auth/login")
    Call<ResponseBody> loginUser(@Body LoginData loginData);

    @POST("auth/getAllRole")
    Call<List<Role>> getAllRoles();

//    Call<List<Role>> getAllRoles();
}
