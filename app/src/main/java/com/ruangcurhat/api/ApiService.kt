package com.ruangcurhat.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<ApiResponse<UserDto>>

    @POST("auth/logout")
    fun logout(@Header("Authorization") authorization: String): Call<ApiResponse<Unit>>

    @PATCH("auth/me")
    fun updateProfile(
        @Header("Authorization") authorization: String,
        @Body request: UpdateProfileRequest
    ): Call<ApiResponse<UserDto>>

    @GET("counselors")
    fun getCounselors(@Query("religion") religion: String? = null): Call<ListResponse<CounselorDto>>

    @POST("counselors")
    fun createCounselor(@Body request: CounselorRequest): Call<ApiResponse<CounselorDto>>

    @DELETE("counselors/{id}")
    fun deleteCounselor(@Path("id") id: Int): Call<ApiResponse<Unit>>
}
