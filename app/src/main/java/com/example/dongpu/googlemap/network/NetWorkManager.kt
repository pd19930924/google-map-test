package com.example.dongpu.googlemap.network

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by dong.pu on 2019/7/19.
 */
class NetWorkManager {

    fun asynNet(){
        var okHttpClient = OkHttpClient()
        var request = Request.Builder().url("http://www.baidu.com").method("GET", null).build()

    }
}