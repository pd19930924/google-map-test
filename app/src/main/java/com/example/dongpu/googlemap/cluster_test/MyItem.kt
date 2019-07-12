package com.example.dongpu.googlemap.cluster_test

import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by dong.pu on 2019/6/27.
 */
class MyItem : ClusterItem{

    private var mPosition : LatLng? = null
    private var mSnippet : String? = null
    private var mTitle : String? = null

    var icon : BitmapDescriptor? = null

    @JvmOverloads
    constructor(markerOptions: MarkerOptions){
        this.mPosition = markerOptions.position
        this.mTitle = markerOptions.title
        this.mSnippet = markerOptions.snippet
        this.icon = markerOptions.icon
    }

    override fun getPosition(): LatLng {
        return mPosition!!
    }

    override fun getSnippet(): String? {
        return mSnippet
    }

    override fun getTitle(): String? {
        return mTitle
    }

}