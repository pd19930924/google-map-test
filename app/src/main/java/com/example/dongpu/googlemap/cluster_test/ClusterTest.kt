package com.example.dongpu.googlemap.cluster_test

import android.content.Context
import com.example.dongpu.googlemap.base_google_map.BaseGoogleMap
import com.google.android.gms.maps.model.LatLng
import java.util.*

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
    fun startDefaultCluster(){
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

    fun startCluster(){
        if(baseGoogleMap.getMarkerList().size == 0){
            return
        }
        baseGoogleMap.startCluster(context)
    }

    fun stopCluster(){
        baseGoogleMap.stopCluster()
    }

    fun hideSomeCluster(){
        if(!baseGoogleMap.getIsStartCluster()) return  //we must make sure tha we have begin cluster
        var size = baseGoogleMap.getMarkerList().size
        for(i in 0..(size-1)){
            var random = Random().nextDouble()
            if(random < 0.6){
                baseGoogleMap.hideMarker(i)
            }
        }
    }
}