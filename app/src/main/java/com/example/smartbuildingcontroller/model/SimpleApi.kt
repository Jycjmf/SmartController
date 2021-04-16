package com.example.smartbuildingcontroller.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SimpleApi {
    @POST("auth")
    suspend fun pushLoginPost(@Body post:LoginPost): Response<ResData>
    @POST("verifyCode")
    suspend fun pushVerifyCodePost(@Body post:VerifyCodeData): Response<ResData>
    @POST("forgetVerifyCode")
    suspend fun pushForgetVerifyCodePost(@Body post:VerifyCodeData): Response<ResData>
    @POST("finishRegister")
    suspend fun pushRegisterPost(@Body post:FinishRegisterData): Response<ResData>
    @POST("forgetPassword")
    suspend fun pushForgetPasswordPost(@Body post:FinishRegisterData): Response<ResData>
}