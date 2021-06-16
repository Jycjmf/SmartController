package com.example.smartbuildingcontroller.view.smokeView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.smartbuildingcontroller.R
import com.example.smartbuildingcontroller.databinding.FragmentFirstSmokeBinding
import com.example.smartbuildingcontroller.viewModel.ConnectToServerViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement


class FragmentFirstSmoke : Fragment() {
    private val model: ConnectToServerViewModel by activityViewModels()
    lateinit var binding:FragmentFirstSmokeBinding
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
        binding= FragmentFirstSmokeBinding.inflate(inflater,container,false)
        binding.binding=model
        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Column)
            .zoomType(AAChartZoomType.XY).gradientColorEnable(true)
            .backgroundColor("#ffffff")
            .colorsTheme(arrayOf("#07C69A","#ffffff"))
            .series(arrayOf(
                AASeriesElement()
                    .name("烟雾")
                    .data(  model.notifyHandler.listCan.smokeListCan.firSmoke.value!!.toArray())
            )
            )
        model.notifyHandler.listCan.smokeListCan.firSmoke.observe(requireActivity(), Observer {
            binding.smokeCharts.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                arrayOf(
                    AASeriesElement().name("烟雾").data(
                        it.toArray()
                    )
                )
            )

        })
        binding.smokeCharts.aa_drawChartWithChartModel(aaChartModel)
        binding.switchSmoke.setOnCheckedChangeListener {
            if (it)
                model.switchStatus.S = "on"
            else
                model.switchStatus.S = "off"
            model.changeSwitchStatus()
        }
        return binding.root
    }

}