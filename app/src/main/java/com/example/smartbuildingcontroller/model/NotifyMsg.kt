package com.example.smartbuildingcontroller.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.aliyun.alink.h2.connection.ConnectionStatus
import com.aliyun.alink.linkkit.api.LinkKit
import com.aliyun.alink.linksdk.cmp.core.base.AMessage
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener
import com.aliyun.alink.linksdk.tools.ALog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class NotifyMsg(private val context: Application) : IConnectNotifyListener {
    var msgType: String = "null"
    var msgTemperature: String = "null"
    var msgSmoke: String = "null"
    var msgHumidity: String = "null"
    var msgId: String = "null"
    var msgDate: String = "null"
    var connectionStatus: String = "在线"
    override fun onNotify(connectId: String, topic: String, aMessage: AMessage) {
        val data = String((aMessage.data as ByteArray)!!)
        ALog.d(
            "Link",
            "onNotify() called with: connectId = [$connectId], topic = [$topic], aMessage = [$data]"
        )
        if (topic.indexOf("/a1gwqeuEjEv/Controller/user/Get") != -1) {
            var res = Gson().fromJson(data, Payload::class.java)
            msgType = res.Type
            if (res.T.isNullOrBlank()) {
                msgTemperature = "N/A"
            } else {
                msgTemperature = res.T
            }
            if (res.S.isNullOrBlank()) {
                msgSmoke = "N/A"
            } else {
                msgSmoke = res.S
            }
            if (res.H.isNullOrBlank()) {
                msgHumidity = "N/A"
            } else {
                msgHumidity = res.H
            }
            msgId = res.UnitId
            val date = Calendar.getInstance().time
            msgDate = SimpleDateFormat("yyyy-MM-dd hh:mm").format(date)
        }
    }

    override fun shouldHandle(p0: String?, p1: String?): Boolean {
        return true
    }

    override fun onConnectStateChange(p0: String?, p1: ConnectState?) {
        if (p1 == ConnectState.CONNECTED)
            connectionStatus = "在线"
        else if (p1 == ConnectState.DISCONNECTED)
            connectionStatus = "离线"
        else if (p1 == ConnectState.CONNECTFAIL)
            connectionStatus = "连接失败"
        else if (p1 == ConnectState.CONNECTING)
            connectionStatus = "连接中"
        else
            connectionStatus = "Unknown"
        Log.d("TAG", "onConnectStateChange: $connectionStatus")
    }

}