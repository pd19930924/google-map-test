package com.example.dongpu.googlemap

import android.provider.ContactsContract
import android.support.v7.widget.DialogTitle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by dong.pu on 2019/6/27.
 */
class MyItem : ClusterItem{

    private var mPosition : LatLng? = null
    private var mSnippet : String? = null
    private var mTitle : String? = null

    @JvmOverloads
    constructor(latLng : LatLng, title: String? = null, snippet : String? = null){
        this.mPosition = latLng
        this.mTitle = title
        this.mSnippet = snippet
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