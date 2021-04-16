package com.example.smartbuildingcontroller.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.smartbuildingcontroller.FragmentDetailDirections
import com.example.smartbuildingcontroller.databinding.FragmentStatusBinding
import com.example.smartbuildingcontroller.viewModel.ConnectToServerViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartZoomType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import render.animations.Render
import render.animations.Zoom

class StatusFragment : BaseFragment() {
    private lateinit var binding: FragmentStatusBinding
    private val model: ConnectToServerViewModel by activityViewModels()
    override var bottomNavigationViewVisibility = View.VISIBLE
    override var topNavigationViewVisibility = View.VISIBLE
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(inflater, container, false)
        binding.cardViewSmoke.setOnClickListener {
            findNavController().navigate(
                StatusFragmentDirections.actionStatusFragmentToFragmentSmokeDeatil()
            )
        }

        binding.cardTemp.setOnClickListener { findNavController().navigate(StatusFragmentDirections.actionStatusFragmentToFragmentTemperatureDetail()) }
        binding.cardHumidity.setOnClickListener {
            findNavController().navigate(
                StatusFragmentDirections.actionStatusFragmentToFragmentHumidityDetail()
            )
        }
        if (!model.isConnected) {
            model.startConnect()
            model.isConnected = true
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(requireContext(), "再按一次退出", Toast.LENGTH_SHORT).show()
                }
            })
        binding.binding = model
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        model.msgHumidity.observe(requireActivity(), { x ->
            if (x != binding.progressHumidity.percent.toString()) {
                try {
                    binding.progressHumidity.percent = x.toFloat()
                } catch (e: Exception) {
                    binding.progressHumidity.percent = 0f
                }

            }
        })
        model.msgSmoke.observe(
            requireActivity(),
            { x ->
                if (x != binding.progressSmoke.percent.toString()) {
                    try {
                        binding.progressSmoke.percent = x.toFloat()
                    } catch (e: Exception) {
                        binding.progressSmoke.percent = 0f
                    }

                }
            })
        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Column)
            .zoomType(AAChartZoomType.XY).gradientColorEnable(true)
            .backgroundColor("#ffffff")
            .colorsTheme(arrayOf("#F96E6E", "#ffffff"))
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("烟雾")
                        .data(
                            model.smokeList.value!!.toArray()
                        )
                )
            )
        binding.statusCharts.aa_drawChartWithChartModel(aaChartModel)
        model.smokeList.observe(requireActivity(), Observer {
            binding.statusCharts.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                arrayOf(
                    AASeriesElement().name("烟雾").data(
                        it.toArray()
                    )
                )
            )

        })
        var render = Render(requireContext())
        render.setDuration(500)
        render.setAnimation(Zoom().In(binding.cardViewSmoke))
        render.start()
        render.setAnimation(Zoom().In(binding.cardTemp))
        render.start()
        render.setAnimation(Zoom().In(binding.cardHumidity))
        render.start()
        render.setAnimation(Zoom().In(binding.cardMainCharts))
        render.start()
    }
}