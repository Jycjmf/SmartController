package com.example.smartbuildingcontroller.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aliyun.alink.dm.api.DeviceInfo
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig
import com.aliyun.alink.linkkit.api.LinkKit
import com.aliyun.alink.linkkit.api.LinkKitInitParams
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentNet
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttSubscribeRequest
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSubscribeListener
import com.aliyun.alink.linksdk.tools.AError
import com.aliyun.alink.linksdk.tools.ALog
import com.example.smartbuildingcontroller.model.DeviceKey
import com.example.smartbuildingcontroller.model.NotifyMsg
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*


class ConnectToServerViewModel(application: Application) : AndroidViewModel(application) {

    var isConnected: Boolean = false
    var notifyHandler = NotifyMsg(getApplication())
    var msgType = MutableLiveData<String>()
    var msgTemperature = MutableLiveData<String>()
    var msgSmoke = MutableLiveData<String>()
    var msgHumidity = MutableLiveData<String>()
    var msgId = MutableLiveData<String>()
    var msgDate = MutableLiveData<String>()
    var msgTemperatureStatus = MutableLiveData<String>()
    var msgFTemperature = MutableLiveData<String>()
    var detailTemperature = MutableLiveData<String>()
    var detailHumidity = MutableLiveData<String>()
    var smokeList = MutableLiveData<LinkedList<Float>>(LinkedList())
    var humidityList = MutableLiveData<LinkedList<Float>>(LinkedList())
    var temperatureList = MutableLiveData<LinkedList<Float>>(LinkedList())
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun startConnect() {
        val deviceInfo = DeviceInfo()
        deviceInfo.productKey = DeviceKey.productKey // 产品类型
        deviceInfo.deviceName = DeviceKey.deviceName // 设备名称
        deviceInfo.deviceSecret = DeviceKey.deviceSecret // 设备密钥
        PersistentNet.getInstance().openLog(true);
        ALog.setLevel(ALog.LEVEL_DEBUG);
        val clientConfig =
            IoTMqttClientConfig(DeviceKey.productKey, DeviceKey.deviceName, DeviceKey.deviceSecret)
        val params = LinkKitInitParams()
        params.deviceInfo = deviceInfo
        params.mqttClientConfig = clientConfig
        LinkKit.getInstance().init(getApplication(), params, object : ILinkKitConnectListener {
            override fun onError(error: AError?) {
                Toast.makeText(getApplication(), error?.msg, Toast.LENGTH_SHORT).show()
            }

            override fun onInitDone(data: Any) {

            }
        })
        viewModelScope.launch {
            delay(1000)
            subscribeTopic()
        }

    }

    fun subscribeTopic() {
        val subscribeRequest = MqttSubscribeRequest()
        subscribeRequest.topic = "/a1gwqeuEjEv/Controller/user/Get"
        subscribeRequest.isSubscribe = true
        subscribeRequest.qos = 0 // 支持0或者1

        LinkKit.getInstance().subscribe(subscribeRequest, object : IConnectSubscribeListener {
            override fun onSuccess() {
                Toast.makeText(getApplication(), "连接阿里云服务器成功", Toast.LENGTH_SHORT).show()
                LinkKit.getInstance().registerOnPushListener(notifyHandler)
                viewModelScope.launch {
                    while (isActive) {
                        msgTemperature.value = notifyHandler.msgTemperature
                        msgId.value = notifyHandler.msgId
                        msgHumidity.value = notifyHandler.msgHumidity
                        msgSmoke.value = notifyHandler.msgSmoke
                        msgType.value = notifyHandler.msgType
                        msgDate.value = notifyHandler.msgDate
                        try {
                            msgFTemperature.value =
                                String.format(
                                    "%.1f",
                                    notifyHandler.msgTemperature.toFloat() * 1.8 + 32
                                )
                            if (msgTemperature.value!!.toFloat() < 10)
                                msgTemperatureStatus.value = "较冷"
                            else if (msgTemperature.value!!.toFloat() < 28)
                                msgTemperatureStatus.value = "舒适"
                            else if (msgTemperature.value!!.toFloat() < 40)
                                msgTemperatureStatus.value = "炎热"
                            else
                                msgTemperatureStatus.value = "高温警告"
                            detailTemperature.value =
                                msgFTemperature.value + " ℉  |  " + msgTemperatureStatus.value
                            if (notifyHandler.msgTemperature.toFloatOrNull() != null) {
                                if (!temperatureList.value!!.any() || temperatureList.value!!.last.toString() != notifyHandler.msgTemperature) {
                                    temperatureList.value!!.add(notifyHandler.msgTemperature.toFloat())
                                    temperatureList.notifyObserver()
                                }
                            }
                        } catch (e: Exception) {
                            msgFTemperature.value = "32"
                            msgTemperatureStatus.value = "较冷"
                        }
                        if (notifyHandler.msgSmoke.toFloatOrNull()!=null)
                        {
                            if (!smokeList.value!!.any()||smokeList.value!!.last.toString()!=notifyHandler.msgSmoke)
                            {
                                smokeList.value!!.add(notifyHandler.msgSmoke.toFloat())
                                smokeList.notifyObserver()
                            }
                        }
                        if (notifyHandler.msgHumidity.toFloatOrNull()!=null)
                        {
                            if (!humidityList.value!!.any()||humidityList.value!!.last.toString()!=notifyHandler.msgHumidity)
                            {
                                humidityList.value!!.add(notifyHandler.msgHumidity.toFloat())
                                humidityList.notifyObserver()
                            }
                        }
                        try {
                            if (msgHumidity.value!!.toFloat() < 30)
                                detailHumidity.value = "${msgDate.value} | 干燥"
                            else if (msgHumidity.value!!.toFloat() < 80)
                                detailHumidity.value = "${msgDate.value} | 舒适"
                            else
                                detailHumidity.value = "${msgDate.value} | 潮湿"
                        } catch (e: java.lang.Exception) {
                            detailHumidity.value = "${msgDate.value} | 干燥"
                        }
                        delay(1000)
                    }
                }
            }

            override fun onFailure(aError: AError) {
                Toast.makeText(getApplication(), aError.msg, Toast.LENGTH_SHORT).show()
            }
        })

    }

}