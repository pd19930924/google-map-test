package com.example.dongpu.googlemap.cluster_test

import android.content.ClipData
import android.content.Context
import com.example.dongpu.googlemap.BaseGoogleMap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import java.nio.channels.FileChannel

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

    /**
     * Here is an example of clustering
     * @param markerList that we are using
     */
    fun clustering(markerList: List<Marker>){
        for(marker in markerList){
            var myItem = MyItem(marker.position)
            clusterManager.addItem(myItem)
        }
    }
}