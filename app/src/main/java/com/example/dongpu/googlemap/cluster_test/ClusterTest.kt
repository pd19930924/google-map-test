package com.example.dongpu.googlemap.cluster_test

import android.content.Context
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
    private lateinit var clusterManager : ClusterManager<MyItem>
    private lateinit var context: Context
    private lateinit var mMap: GoogleMap

    constructor(context: Context, mMap : GoogleMap){
        this.context = context
        this.mMap = mMap
        this.clusterManager = ClusterManager<MyItem>(context, mMap)
    }


    //Here is an example of clustering
    //this example is come from google
    fun clustering(){
        var lat = 40.721270
        var lng = -73.982380
        var i = 0
        while(i<10){
            var offset = i/60.0
            lat = lat+offset
            lng = lng+offset
            var myItem = MyItem(LatLng(lat,lng))
            clusterManager!!.addItem(myItem)
            i++
        }
        mMap.setOnCameraIdleListener(clusterManager)
    }
}