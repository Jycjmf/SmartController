package com.example.smartbuildingcontroller.view.tempView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.smartbuildingcontroller.R
import com.example.smartbuildingcontroller.databinding.FragmentFirTempBinding
import com.example.smartbuildingcontroller.databinding.FragmentFirstSmokeBinding
import com.example.smartbuildingcontroller.viewModel.ConnectToServerViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement


class FragmentFirstTemp : Fragment() {
    private val model: ConnectToServerViewModel by activityViewModels()
    lateinit var binding:FragmentFirTempBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner=this
        if (model.switchStatus.S == "on")
            binding.switchSmoke.setChecked(true)
        else
            binding.switchSmoke.setChecked(false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentFirTempBinding.inflate(inflater,container,false)
        binding.binding=model
        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .zoomType(AAChartZoomType.XY).gradientColorEnable(true)
            .backgroundColor("#ffffff")
            .colorsTheme(arrayOf("#93F3EF", "#ffffff"))
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("温度")
                        .data(model.notifyHandler.listCan.tempListCan.firTemp.value!!.toArray())
                )
            )
        model.notifyHandler.listCan.tempListCan.firTemp.observe(requireActivity(), Observer {
            binding.tempCharts.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                arrayOf(
                    AASeriesElement().name("温度").data(
                        it.toArray()
                    )
                )
            )

        })
        binding.tempCharts.aa_drawChartWithChartModel(aaChartModel)

        return binding.root
    }

}