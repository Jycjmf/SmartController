package com.example.smartbuildingcontroller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartbuildingcontroller.view.BaseFragment


class FragmentFunction : BaseFragment() {
    override var topNavigationViewVisibility = View.GONE
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_function, container, false)
    }


}