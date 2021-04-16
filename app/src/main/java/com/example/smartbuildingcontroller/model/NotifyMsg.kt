package com.example.smartbuildingcontroller.model

import android.app.Application
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.aliyun.alink.linkkit.api.LinkKit
import com.aliyun.alink.linksdk.cmp.api.ConnectSDK
import com.aliyun.alink.linksdk.cmp.core.base.AMessage
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener
import com.aliyun.alink.linksdk.tools.ALog
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class NotifyMsg(private val context: Application) : IConnectNotifyListener {
    var msgType: String = "null"
    var msgTemperature: String = "null"
    var msgSmoke: String = "null"
    var msgHumidity: String = "null"
    var msgId: String = "null"
    var msgDate:String="null"

    override fun onNotify(connectId: String, topic: String, aMessage: AMessage) {
        val data = String((aMessage.data as ByteArray)!!)
        ALog.d(
            "Link",
            "onNotify() called with: connectId = [$connectId], topic = [$topic], aMessage = [$data]"
        )
        if (topic.indexOf("/a1gwqeuEjEv/Controller/user/Get") != -1) {
            var res = Gson().fromJson(data, Payload::class.java)
            msgType = res.Type
            msgTemperature = res.T
            msgSmoke = res.S
            msgHumidity = res.H
            msgId = res.UnitId
            val date = Calendar.getInstance().time
            msgDate = SimpleDateFormat("今天 hh:mm").format(date)
        }
    }

    override fun shouldHandle(p0: String?, p1: String?): Boolean {
        return true
    }

    override fun onConnectStateChange(p0: String?, p1: ConnectState?) {
        LinkKit.getInstance().registerOnPushListener(NotifyMsg(context));
    }

}