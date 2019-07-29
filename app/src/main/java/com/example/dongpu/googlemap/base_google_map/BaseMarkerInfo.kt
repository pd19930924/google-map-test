package com.example.dongpu.googlemap.base_google_map

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng

/**
 * Created by dong.pu on 2019/7/29.
 */
open class BaseMarkerInfo {

    open var position : LatLng? = null  //The latlng must exist
    var title : String? = null
    var snippets : String? = null
    var icon : BitmapDescriptor? = null
    var isDragable : Boolean = false
    var isShowInfoWindow : Boolean = false
    var tag : Int? = null

    /*
    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        if(!(other is BaseMarkerInfo)){
            return false
        }else{
            return true
        }
    }*/
}