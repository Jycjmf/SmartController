package com.example.smartbuildingcontroller.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartbuildingcontroller.model.LoginPost
import com.example.smartbuildingcontroller.model.Repository
import com.example.smartbuildingcontroller.model.ResData
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(application: Application) :AndroidViewModel(application) {
    var response=MutableLiveData<Response<ResData>>()

suspend fun pushPost(post:LoginPost){
        var repository=Repository()
         response.value=repository.pushLoginPost(post)
}
}