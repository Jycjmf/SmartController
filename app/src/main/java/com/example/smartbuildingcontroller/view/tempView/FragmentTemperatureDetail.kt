package com.example.smartbuildingcontroller.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartbuildingcontroller.R
import com.example.smartbuildingcontroller.databinding.FragmentTemperatureDetailBinding
import com.example.smartbuildingcontroller.model.HistoryDatabase
import com.example.smartbuildingcontroller.viewModel.ConnectToServerViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentTemperatureDetail : BaseFragment() {
    override var bottomNavigationViewVisibility = View.GONE
    lateinit var binding: FragmentTemperatureDetailBinding
    override var topNavigationViewVisibility = View.GONE
    private val model: ConnectToServerViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTemperatureDetailBinding.inflate(inflater, container, false)
        binding.binding = model
        binding.smokeDetailToolbar.inflateMenu(R.menu.smoke_deatil_menu)
        binding.smokeDetailToolbar.setNavigationIcon(R.drawable.ic_back)
        binding.smokeDetailToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.all_smoke_detail_item -> {
                    findNavController().navigate(R.id.action_global_fragmentSmokeRecord)
                    true
                }
                else -> false
            }
        }
        binding.smokeDetailToolbar.setNavigationOnClickListener {
            findNavController().navigate(
                FragmentTemperatureDetailDirections.actionFragmentTemperatureDetailToStatusFragment()
            )
        }
        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .zoomType(AAChartZoomType.XY).gradientColorEnable(true)
            .backgroundColor("#ffffff")
            .colorsTheme(arrayOf("#93F3EF", "#ffffff"))
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("温度")
                        .data(arrayOf(1))
                )
            )
//        model.temperatureList.observe(requireActivity(), Observer {
//            binding.smokeCharts.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
//                arrayOf(
//                    AASeriesElement().name("温度").data(
//                        arrayOf(1)
//                    )
//                )
//            )
//
//        })
//        binding.smokeCharts.aa_drawChartWithChartModel(aaChartModel)
//        binding.switchTemp.setOnCheckedChangeListener {
//            if (it)
//                model.switchStatus.T = "on"
//            else
//                model.switchStatus.T = "off"
//            model.changeSwitchStatus()
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        if (model.switchStatus.T == "on")
            binding.switchTemp.setChecked(true)
        else
            binding.switchTemp.setChecked(false)
    }

}