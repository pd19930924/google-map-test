package com.example.dongpu.googlemap

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * Created by dong.pu on 2019/7/8.
 */
class CircleTest {

    private lateinit var baseGoogleMap : BaseGoogleMap

    lateinit var centerLatLng : LatLng
    var radius : Double = 0.0

    constructor(baseGoogleMap: BaseGoogleMap){
        this.baseGoogleMap = baseGoogleMap
    }

    fun drawDefaultCircle(){
        baseGoogleMap.drawCircleOnMap(centerLatLng , radius1)
    }

    fun drawDrawableCircle(color : Int){
        baseGoogleMap.drawCircleOnMap(centerLatLng, radius2, color)
    }

    companion object {
        //circle radius
        val radius1 = 180000.0
        val radius2 = 220000.0
    }
}