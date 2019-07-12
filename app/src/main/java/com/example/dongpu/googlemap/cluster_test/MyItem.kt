package com.example.dongpu.googlemap.cluster_test

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.ContactsContract
import android.support.v7.widget.DialogTitle
import com.example.dongpu.googlemap.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by dong.pu on 2019/6/27.
 */
class MyItem : ClusterItem{

    private var mPosition : LatLng? = null
    private var mSnippet : String? = null
    private var mTitle : String? = null

    constructor(marker: Marker){
        this.mPosition = marker.position
        this.mTitle = marker.title
        this.mSnippet = marker.snippet
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