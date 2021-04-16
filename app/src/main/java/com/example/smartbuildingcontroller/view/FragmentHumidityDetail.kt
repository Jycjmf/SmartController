package com.example.smartbuildingcontroller.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.smartbuildingcontroller.R
import com.example.smartbuildingcontroller.databinding.FragmentHumidityDetailBinding
import com.example.smartbuildingcontroller.viewModel.ConnectToServerViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

import kotlin.collections.ArrayList

class FragmentHumidityDetail : BaseFragment() {
    lateinit var binding: FragmentHumidityDetailBinding
    private val model: ConnectToServerViewModel by activityViewModels()
    override var bottomNavigationViewVisibility = View.GONE
    override var topNavigationViewVisibility = View.GONE
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHumidityDetailBinding.inflate(inflater, container, false)
        binding.binding=model
        binding.smokeDetailToolbar.inflateMenu(R.menu.humi_menu)
        binding.smokeDetailToolbar.setNavigationIcon(R.drawable.ic_back)
        binding.smokeDetailToolbar.setNavigationOnClickListener { findNavController().navigate(FragmentHumidityDetailDirections.actionFragmentHumidityDetailToStatusFragment()) }
        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .zoomType(AAChartZoomType.XY).gradientColorEnable(true)
            .backgroundColor("#ffffff")
            .colorsTheme(arrayOf("#C2ACFF","#ffffff"))
            .series(arrayOf(
                AASeriesElement()
                    .name("湿度")
                    .data(model.humidityList.value!!.toArray())
            )
            )
        model.humidityList.observe(requireActivity(), Observer {
            binding.smokeCharts.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                arrayOf(
                    AASeriesElement().name("湿度").data(
                        it.toArray()
                    )
                )
            )

        })
        binding.smokeCharts.aa_drawChartWithChartModel(aaChartModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner=this
    }
}