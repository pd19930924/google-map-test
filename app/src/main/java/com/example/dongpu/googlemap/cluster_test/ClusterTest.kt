package com.example.dongpu.googlemap.cluster_test

import android.content.Context
import com.example.dongpu.googlemap.BaseGoogleMap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager

/**
 * Created by dong.pu on 2019/7/11.
 * This is used to show how cluster works
 * at first ,we define a class named MyItem to store our item
 */
class ClusterTest {
    //This is main function for clusting
    private lateinit var context: Context
    private lateinit var baseGoogleMap: BaseGoogleMap

    constructor(context: Context, baseGoogleMap: BaseGoogleMap){
        this.context = context
        this.baseGoogleMap = baseGoogleMap
    }


    //Here is an example of clustering
    //this example is come from google
    fun startCluster(){
        var lat = 40.721270
        var lng = -73.982380
        var i = 0
        while(i<10){
            var offset = i/60.0
            lat = lat+offset
            lng = lng+offset
            baseGoogleMap.addMarkerToMap(LatLng(lat, lng))
            i++
        }
        baseGoogleMap.startCluster(context)
    }

    fun stopCluster(){
        baseGoogleMap.stopCluster()
    }
}