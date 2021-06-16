package com.example.smartbuildingcontroller.view

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.smartbuildingcontroller.R
import com.example.smartbuildingcontroller.databinding.FragmentRegisterBinding
import com.example.smartbuildingcontroller.model.FinishRegisterData
import com.example.smartbuildingcontroller.model.Repository
import com.example.smartbuildingcontroller.model.ResData
import com.example.smartbuildingcontroller.model.VerifyCodeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import render.animations.Attention
import render.animations.Render
import retrofit2.Response
import java.lang.Exception


class fragmentRegister : BaseFragment() {
    private lateinit var binding: FragmentRegisterBinding
    override var bottomNavigationViewVisibility = View.GONE
    override var topNavigationViewVisibility = View.GONE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.registerToolbar.setNavigationIcon(R.drawable.ic_back)
        binding.registerToolbar.setNavigationOnClickListener { findNavController().navigate(fragmentRegisterDirections.actionFragmentRegisterToLoginFragment()) }
        binding.buttonSend.setOnClickListener {
            if (!verifyEmailData()){
                binding.buttonRegister.doResult(false)
                return@setOnClickListener
            }
            binding.buttonSend.isEnabled=false
            object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.buttonSend.text = "${millisUntilFinished / 1000}s"
                }

                override fun onFinish() {
                  binding.buttonSend.text="发送"
                    binding.buttonSend.isEnabled=true
                }
            }.start()

            var post=VerifyCodeData(binding.editEmail.text.toString())
            var repository=Repository()
            var resData:Response<ResData>?=null
            var res=lifecycleScope.launch {
                try{
                resData  = repository.pushVerifyCodePost(post)
                }catch (e:Exception)
                {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    binding.buttonRegister.doResult(false)
                    return@launch
                }
                }
            lifecycleScope.launch {
                res.join()
                if (resData?.isSuccessful==true)
                    if (resData?.body()?.Code==402)
                    {
                        Toast.makeText(requireContext(), "该邮箱已被注册", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        binding.buttonRegister.setOnClickListener {
         //   binding.buttonRegister.startLoading()
            if (!verifyNotEmpty()){
                binding.buttonRegister.doResult(false)
                return@setOnClickListener
            }
            if (binding.editConfirmPassword.text.toString()!=binding.editPassword.text.toString())
            {
                Toast.makeText(requireContext(), "两次密码不一致", Toast.LENGTH_SHORT).show()
                var render = Render(requireContext())
                render.setAnimation(Attention().Shake(binding.editPassword))
                render.setDuration(500)
                render.start()
                render.setAnimation(Attention().Shake(binding.editConfirmPassword))
                render.start()
                binding.buttonRegister.doResult(false)
                return@setOnClickListener
            }
            var post=FinishRegisterData(binding.editUser.text.toString(),binding.editPassword.text.toString(),binding.editEmail.text.toString(),binding.editCode.text.toString())
            var repository=Repository()
            var res:Response<ResData>?=null
            var coroutineRes=lifecycleScope.launch {
                try {
                    res  = repository.pushRegisterPost(post)
                }catch (e:Exception)
                {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    binding.buttonRegister.doResult(false)
                    return@launch
                }

            }
            lifecycleScope.launch {
                coroutineRes.join()
                if (res?.isSuccessful==true)
                {
                    if (res?.body()?.Code==200)
                    {
                        Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show()
                        binding.buttonRegister.doResult(true)
                        delay(500)
                        findNavController().navigate(fragmentRegisterDirections.actionFragmentRegisterToLoginFragment())
                        return@launch
                    }
                    else if(res?.body()?.Code==403)
                    {
                        Toast.makeText(requireContext(), "请先获得验证码", Toast.LENGTH_SHORT).show()
                    }
                    else if (res?.body()?.Code==405)
                    {
                        Toast.makeText(requireContext(), "验证码错误", Toast.LENGTH_SHORT).show()
                    }
                        binding.buttonRegister.doResult(false)
                }
                else
                    Toast.makeText(requireContext(), res?.code().toString(), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
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