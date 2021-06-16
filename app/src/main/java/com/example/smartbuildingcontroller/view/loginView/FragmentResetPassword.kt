package com.example.smartbuildingcontroller.view

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.smartbuildingcontroller.view.FragmentResetPasswordDirections
import com.example.smartbuildingcontroller.R
import com.example.smartbuildingcontroller.databinding.FragmentResetPasswordBinding
import com.example.smartbuildingcontroller.model.FinishRegisterData
import com.example.smartbuildingcontroller.model.Repository
import com.example.smartbuildingcontroller.model.ResData
import com.example.smartbuildingcontroller.model.VerifyCodeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import render.animations.Attention
import render.animations.Render


import retrofit2.Response

class FragmentResetPassword : BaseFragment() {
    private lateinit var binding: FragmentResetPasswordBinding
    override var bottomNavigationViewVisibility = View.GONE
    override var topNavigationViewVisibility = View.GONE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        binding.registerToolbar.setNavigationIcon(R.drawable.ic_back)
        binding.registerToolbar.setNavigationOnClickListener { findNavController().navigate(FragmentResetPasswordDirections.actionFragmentResetPasswordToLoginFragment()) }
        binding.buttonSend.setOnClickListener {
            if (verifyEmailData())
                getVerifyCode(VerifyCodeData(binding.editEmail.text.toString()))
        }
        binding.buttonRegister.setOnClickListener {
            finishRegister(
                FinishRegisterData(
                    "null",
                    binding.editPassword.text.toString(),
                    binding.editEmail.text.toString(),
                    binding.editCode.text.toString()
                )
            )
        }
        return binding.root
    }

    fun getVerifyCode(post: VerifyCodeData) {
        startCountDown()
        var repository = Repository()
        var resData: Response<ResData>? = null
        var res = lifecycleScope.launch {
            try {
                resData = repository.pushForgetVerifyCodePost(post)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "${e.message}请检查网络连接", Toast.LENGTH_SHORT).show()
            }

        }
        lifecycleScope.launch {
            res.join()
            if (resData?.isSuccessful == true)
                if (resData?.body()?.Code == 406) {
                    Toast.makeText(requireContext(), "用户不存在", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun startCountDown() {
        binding.buttonSend.isEnabled = false
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.buttonSend.text = "${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                binding.buttonSend.text = "发送"
                binding.buttonSend.isEnabled = true
            }
        }.start()
    }

    fun finishRegister(post: FinishRegisterData) {
        if (!verifyNotEmpty()) {
            binding.buttonRegister.doResult(false)
            return
        }
        if (binding.editPassword.text.toString()!=binding.editConfirmPassword.text.toString())
        {
            Toast.makeText(requireContext(), "密码输入不一致", Toast.LENGTH_SHORT).show()
            var render = Render(requireContext())
            render.setAnimation(Attention().Shake(binding.editPassword))
            render.setDuration(500)
            render.start()
            render.setAnimation(Attention().Shake(binding.editConfirmPassword))
            render.start()
            binding.buttonRegister.doResult(false)
            return
        }

        var repository = Repository()
        var resData: Response<ResData>? = null
        var res = lifecycleScope.launch {
            try {
                resData = repository.pushForgetPasswordPost(post)
            }catch (e:Exception){
                Toast.makeText(requireContext(), "${e.message}请检查网络连接", Toast.LENGTH_SHORT).show()
                binding.buttonRegister.doResult(false)
            }

        }
        lifecycleScope.launch {
            res.join()
            if (resData?.isSuccessful == true) {
                if (resData?.body()?.Code == 200) {
                    binding.buttonRegister.doResult(true)
                    delay(300)
                    findNavController().navigate(FragmentResetPasswordDirections.actionFragmentResetPasswordToLoginFragment())
                    return@launch
                }
                if (resData?.body()?.Code == 403) {
                    Toast.makeText(requireContext(), "请先获得验证码", Toast.LENGTH_SHORT).show()
                    binding.buttonRegister.doResult(false)
                    return@launch
                }
                if (resData?.body()?.Code == 405) {
                    Toast.makeText(requireContext(), "验证码错误", Toast.LENGTH_SHORT).show()
                    binding.buttonRegister.doResult(false)
                    return@launch
                }
            } else
                Toast.makeText(requireContext(), resData?.message(), Toast.LENGTH_SHORT).show()
            binding.buttonRegister.doResult(false)
        }
    }

    fun verifyEmailData() :Boolean{
        val myForm=form {
            input(binding.editEmail) {
                isEmail().description("请输入正确的邮箱")
            }
        }.validate()
        return myForm.success()
    }
    fun verifyNotEmpty():Boolean{
        val myForm=form {
            input(binding.editPassword) {
              isNotEmpty().description("密码不可为空")
            }
        }.validate()
        val confirmForm=form {
            input(binding.editConfirmPassword) {
                isNotEmpty().description("密码不可为空")
            }
        }.validate()
        return myForm.success()&&confirmForm.success()
    }
}