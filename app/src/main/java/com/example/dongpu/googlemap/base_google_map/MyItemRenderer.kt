package com.example.dongpu.googlemap.base_google_map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * Created by dong.pu on 2019/7/29.
 */
open class MyItemRenderer : DefaultClusterRenderer<MyItem> {
    constructor(context : Context, mMap: GoogleMap, clusterManager: ClusterManager<MyItem>) : super(context,mMap, clusterManager)

    override fun onBeforeClusterItemRendered(item: MyItem?, markerOptions: MarkerOptions?) {
        var myMarkerOptions = item!!.markerOptions!!
        markerOptions!!.title(myMarkerOptions.title)
        markerOptions!!.snippet(myMarkerOptions.snippet)
        markerOptions!!.icon(myMarkerOptions.icon)
    }

    override fun onBeforeClusterRendered(cluster: Cluster<MyItem>?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterRendered(cluster, markerOptions)  //default pic
    }

    override fun shouldRenderAsCluster(cluster: Cluster<MyItem>?): Boolean {
        return cluster!!.size > 1
    }
}