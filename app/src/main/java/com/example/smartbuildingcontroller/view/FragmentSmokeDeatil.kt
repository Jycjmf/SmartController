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
import com.example.smartbuildingcontroller.databinding.FragmentSmokeDeatilBinding
import com.example.smartbuildingcontroller.viewModel.ConnectToServerViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

class FragmentSmokeDeatil : BaseFragment() {
    private val model: ConnectToServerViewModel by activityViewModels()
lateinit var binding:FragmentSmokeDeatilBinding
    override var bottomNavigationViewVisibility = View.GONE
    override var topNavigationViewVisibility = View.GONE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner=this
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSmokeDeatilBinding.inflate(inflater,container,false)
        binding.binding=model
        binding.smokeDetailToolbar.setNavigationIcon(R.drawable.ic_back)
        binding.smokeDetailToolbar.setNavigationOnClickListener { findNavController().navigate(FragmentSmokeDeatilDirections.actionFragmentSmokeDeatilToStatusFragment()) }
        binding.smokeDetailToolbar.inflateMenu(R.menu.smoke_deatil_menu)
        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Column)
            .zoomType(AAChartZoomType.XY).gradientColorEnable(true)
            .backgroundColor("#ffffff")
            .colorsTheme(arrayOf("#07C69A","#ffffff"))
            .series(arrayOf(
                AASeriesElement()
                    .name("烟雾")
                    .data(model.smokeList.value!!.toArray())
            )
            )
        model.smokeList.observe(requireActivity(), Observer {
            binding.smokeCharts.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                arrayOf(
                    AASeriesElement().name("烟雾").data(
                        it.toArray()
                    )
                )
            )

        })
        binding.smokeCharts.aa_drawChartWithChartModel(aaChartModel)
        return binding.root
    }


}