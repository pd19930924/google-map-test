package com.example.dongpu.googlemap.base_google_map

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by dong.pu on 2019/7/29.
 */
open class MyItem : ClusterItem {
    private var mPosition : LatLng? = null
    private var mSnippet : String? = null
    private var mTitle : String? = null

    var markerOptions : MarkerOptions? = null

    @JvmOverloads
    constructor(markerOptions: MarkerOptions){
        this.mPosition = markerOptions.position
        this.mTitle = markerOptions.title
        this.mSnippet = markerOptions.snippet
        this.markerOptions = markerOptions
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