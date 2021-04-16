package com.example.smartbuildingcontroller.view

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.smartbuildingcontroller.R
import com.example.smartbuildingcontroller.databinding.FragmentLoginBinding
import com.example.smartbuildingcontroller.model.LoginPost
import com.example.smartbuildingcontroller.model.ResData
import com.example.smartbuildingcontroller.viewModel.LoginViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import render.animations.*
import java.lang.Exception


class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    override var bottomNavigationViewVisibility = View.GONE
    override var topNavigationViewVisibility = View.GONE
    private lateinit var model: LoginViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        var render = Render(requireContext())
        render.setAnimation(Zoom().In(binding.mainCard))
        render.setDuration(500)
        render.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.buttonLogin.setOnClickListener {
            binding.buttonLogin.startLoading()
            tryToLogin()
        }
        binding.textviewForget.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFragmentResetPassword())
        }
        binding.textRegister.setOnClickListener {findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFragmentRegister())}
        return binding.root
    }

    fun tryToLogin() {
        var post =
            LoginPost(binding.editTextUser.text.toString(), binding.editPassword.text.toString())
        lifecycleScope.launch {
            var res = lifecycleScope.launch {
              try {
                  model.pushPost(post)
              }catch (e:Exception)
              {
                  Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                  binding.buttonLogin.doResult(false)
                  return@launch
              }

            }
            res.join()
            if (model.response.value?.isSuccessful == true) {
                if (model.response.value?.body()?.Code == 200) {
                    binding.buttonLogin.doResult(true)
                    lifecycleScope.launch {
                        delay(500)
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToStatusFragment())
                    }
                } else {
                    binding.buttonLogin.doResult(false)
                    if (model.response.value?.body()?.Code == 401)
                        Toast.makeText(requireContext(), "密码错误", Toast.LENGTH_SHORT).show()
                    else if (model.response.value?.body()?.Code == 400)
                        Toast.makeText(requireContext(), "用户名不存在", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
    fun verifyEmailData() :Boolean{
        val myForm=form {
            input(binding.editTextUser) {
                isEmail().description("请输入正确的用户名")
            }
        }.validate()
        return myForm.success()
    }

}
