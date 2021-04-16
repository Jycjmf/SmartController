package com.example.smartbuildingcontroller.model

import retrofit2.Response

class Repository {
    suspend fun pushLoginPost(post: LoginPost):Response<ResData>{
        return RetrofitInstance.api.pushLoginPost(post)
    }
    suspend fun pushVerifyCodePost(post: VerifyCodeData):Response<ResData>{
        return RetrofitInstance.api.pushVerifyCodePost(post)
    }
    suspend fun pushForgetVerifyCodePost(post: VerifyCodeData):Response<ResData>{
        return RetrofitInstance.api.pushForgetVerifyCodePost(post)
    }
    suspend fun pushRegisterPost(post: FinishRegisterData):Response<ResData>{
        return RetrofitInstance.api.pushRegisterPost(post)
    }
    suspend fun pushForgetPasswordPost(post: FinishRegisterData):Response<ResData>{
        return RetrofitInstance.api.pushForgetPasswordPost(post)
    }
}